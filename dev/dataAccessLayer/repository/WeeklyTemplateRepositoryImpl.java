package dataAccessLayer.repository;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dataAccessLayer.dao.WeeklyTemplateDAO;
import dataAccessLayer.dao.WeeklyTemplateItemDAO;
import domainLayerSuppliers.*;
import dto.WeeklyTemplateDTO;
import dto.WeeklyTemplateItemDTO;

public class WeeklyTemplateRepositoryImpl implements WeeklyTemplateRepository {

    private final WeeklyTemplateDAO templateDAO;
    private final WeeklyTemplateItemDAO itemDAO;
    private final SupplierRepository supplierRepository;

    private final List<WeeklyTemplate> templatesCache = new ArrayList<>();

    public WeeklyTemplateRepositoryImpl(
        WeeklyTemplateDAO templateDAO,
        WeeklyTemplateItemDAO itemDAO,
        SupplierRepository supplierRepository
    ) throws SQLException {
        this.templateDAO = templateDAO;
        this.itemDAO = itemDAO;
        this.supplierRepository = supplierRepository;
        loadCacheFromDB();
    }

    private void loadCacheFromDB() throws SQLException {
        templatesCache.clear();
        List<WeeklyTemplateDTO> dtos = templateDAO.getAll();

        for (WeeklyTemplateDTO dto : dtos) {
            Supplier supplier = supplierRepository.getSupplier(dto.sID()).get();
            if (supplier == null) continue;

            Optional<WeeklyTemplate> templateOpt = buildTemplateFromDTO(dto, supplier);
            templateOpt.ifPresent(templatesCache::add);
        }
    }

    private Optional<WeeklyTemplate> buildTemplateFromDTO(WeeklyTemplateDTO dto, Supplier supplier) throws SQLException {
        SupplierAgreement agreement = supplier.getAgreements()
            .stream()
            .filter(a -> a.getAgreementID().equals(dto.agreementID()))
            .findFirst()
            .orElse(null);

        if (agreement == null) return Optional.empty();

        List<WeeklyTemplateItemDTO> items = itemDAO.getAll().stream()
            .filter(i -> i.templateId().equals(dto.templateId()))
            .toList();

        List<ProductRes> products = new ArrayList<>();
        for (WeeklyTemplateItemDTO item : items) {
            SupplierProduct product = agreement.searchProduct(item.productSnum());
            if (product != null) {
                products.add(new ProductRes(product, item.quantity()));
            }
        }

        WeeklyTemplate template = new WeeklyTemplate(
            dto.templateId(),
            supplier,
            products,
            agreement,
            LocalDate.parse(dto.reservationDate()),
            DayOfWeek.valueOf(dto.supplyDay().toUpperCase())
        );
        template.setNextReservationDate(LocalDate.parse(dto.nextReservationDate()));

        return Optional.of(template);
    }

    @Override
    public void saveTemplate(WeeklyTemplate template) throws SQLException {
        WeeklyTemplateDTO dto = new WeeklyTemplateDTO(
            template.getTemplateId(),
            template.getSupplier().getSID(),
            template.getTemplateAgreement().getAgreementID(),
            template.getReservationDate().toString(),
            template.getNextReservationDate().toString(),
            template.getSupplyDay().toString()
        );

        templateDAO.insert(dto);

        for (ProductRes pr : template.getTemplateProducts()) {
            WeeklyTemplateItemDTO itemDTO = new WeeklyTemplateItemDTO(
                template.getTemplateId(),
                pr.getProduct().getProductNum(),
                pr.getQuantity()
            );
            itemDAO.insert(itemDTO);
        }

        templatesCache.add(template);
    }

    @Override
    public Optional<WeeklyTemplate> getTemplate(String templateId, Supplier supplier) throws SQLException {
        return templatesCache.stream()
            .filter(t -> t.getTemplateId().equals(templateId) && t.getSupplier().getSID().equals(supplier.getSID()))
            .findFirst();
    }

    @Override
    public List<WeeklyTemplate> getAllTemplates(Supplier supplier) {
        return templatesCache.stream()
            .filter(t -> t.getSupplier().getSID().equals(supplier.getSID()))
            .toList();
    }

    @Override
    public void deleteTemplate(String templateId) throws SQLException {
        List<WeeklyTemplateItemDTO> items = itemDAO.getAll();
        for (WeeklyTemplateItemDTO item : items) {
            if (item.templateId().equals(templateId)) {
                itemDAO.delete(templateId, item.productSnum());
            }
        }

        templateDAO.delete(templateId);
        templatesCache.removeIf(t -> t.getTemplateId().equals(templateId));
    }

    @Override
    public void updateTemplate(WeeklyTemplate template) throws SQLException {
        WeeklyTemplateDTO dto = new WeeklyTemplateDTO(
            template.getTemplateId(),
            template.getSupplier().getSID(),
            template.getTemplateAgreement().getAgreementID(),
            template.getReservationDate().toString(),
            template.getNextReservationDate().toString(),
            template.getSupplyDay().toString()
        );

        templateDAO.update(dto);

        // Replace in cache
        templatesCache.removeIf(t -> t.getTemplateId().equals(template.getTemplateId()));
        templatesCache.add(template);
    }
}
