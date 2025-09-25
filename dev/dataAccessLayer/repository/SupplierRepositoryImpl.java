package dataAccessLayer.repository;

import dataAccessLayer.dao.*;
import dto.*;
import domainLayerSuppliers.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierDAO supplierDAO;
    private final SupplyDayDAO supplyDayDAO;
    private final PaymentConditionDAO paymentConditionDAO;
    private final ContactPersonDAO contactPersonDAO;
    private final SupplierAgreementDAO supplierAgreementDAO;
    private final SupplierAgreementProductDAO agreementProductDAO;
    private final ProductAgreementDAO productAgreementDAO;

    private final List<Supplier> suppliersCache;

    public SupplierRepositoryImpl(SupplierDAO supplierDAO,
                                   SupplyDayDAO supplyDayDAO,
                                   PaymentConditionDAO paymentConditionDAO,
                                   ContactPersonDAO contactPersonDAO,
                                   SupplierAgreementDAO supplierAgreementDAO,
                                   SupplierAgreementProductDAO agreementProductDAO,
                                   ProductAgreementDAO productAgreementDAO) throws SQLException {
        this.supplierDAO = supplierDAO;
        this.supplyDayDAO = supplyDayDAO;
        this.paymentConditionDAO = paymentConditionDAO;
        this.contactPersonDAO = contactPersonDAO;
        this.supplierAgreementDAO = supplierAgreementDAO;
        this.agreementProductDAO = agreementProductDAO;
        this.productAgreementDAO = productAgreementDAO;

        this.suppliersCache = new ArrayList<>();
        loadCacheFromDB();
    }

    private void loadCacheFromDB() throws SQLException {
        suppliersCache.clear();
        List<SupplierDTO> supplierDTOs = supplierDAO.getAll();

        for (SupplierDTO dto : supplierDTOs) {
            Supplier supplier = createSupplierFromDTO(dto);
            suppliersCache.add(supplier);
        }
    }

    private Supplier createSupplierFromDTO(SupplierDTO dto) throws SQLException {
        String sID = dto.sID();

        // Load supply days
        List<DayOfWeek> supplyDays = supplyDayDAO.getBySupplier(sID).stream()
            .map(sdDTO -> DayOfWeek.valueOf(sdDTO.day().toUpperCase()))
            .collect(Collectors.toList());

        // Load payment condition
        PaymentConditionDTO pcDTO = paymentConditionDAO.get(sID).get();
        PaymentCondition paymentCondition = null;
        paymentCondition = new PaymentCondition(
            PaymentCondition.PaymentType.valueOf(pcDTO.paymentType()),
            pcDTO.bankAccount()
        );

        // Load contact persons
        List<ContactPerson> contactPersons = contactPersonDAO.getBySupplier(sID).stream()
            .map(cpDTO -> new ContactPerson(cpDTO.name(), cpDTO.phoneNumber(), cpDTO.email()))
            .collect(Collectors.toList());

        // Load supplier agreements with their products and discounts
        List<SupplierAgreement> agreements = new ArrayList<>();
        List<SupplierAgreementDTO> agreementDTOs = supplierAgreementDAO.getBySupplier(sID);

        for (SupplierAgreementDTO agrDTO : agreementDTOs) {
            String agreementId = agrDTO.agreementID();

            // Load products in agreement
            List<SupplierProduct> products = agreementProductDAO.getByAgreement(agreementId).stream()
                .map(prodDTO -> new SupplierProduct(prodDTO.productName(),prodDTO.basePrice(), prodDTO.productSnum()))
                .collect(Collectors.toList());

            // Load product discounts
            List<ProductAgreement> discounts = productAgreementDAO.getByAgreement(agreementId).stream()
                .map(paDTO -> {
                    SupplierProduct product = products.stream()
                        .filter(p -> p.getProductNum().equals(paDTO.productSnum()))
                        .findFirst()
                        .orElse(null);

                    return new ProductAgreement(product, paDTO.minQuantity(), paDTO.discountPercent());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            // Find contact person for agreement
            ContactPerson contactPerson = contactPersons.stream()
                .filter(cp -> cp.getName().equals(agrDTO.contactName()))
                .findFirst()
                .orElse(null);

            SupplierAgreement agreement = new SupplierAgreement(
                agreementId,
                contactPerson,
                discounts,
                products,
                agrDTO.hasRegularDays()
            );

            agreements.add(agreement);
        }

        // Construct the Supplier domain object
        return new Supplier(
            dto.name(),
            sID,
            dto.address(),
            paymentCondition,
            contactPersons,
            agreements,
            supplyDays
        );
    }

    @Override
    public void saveSupplier(Supplier supplier) throws SQLException {
        SupplierDTO supplierDTO = new SupplierDTO(supplier.getSID(), supplier.getName(), supplier.getAddress());
        supplierDAO.insert(supplierDTO);

        for (DayOfWeek day : supplier.getSupplyDays()) {
            supplyDayDAO.insert(new SupplyDayDTO(supplier.getSID(), day.toString()));
        }

        PaymentCondition pc = supplier.getPaymentCondition();
        paymentConditionDAO.insert(new PaymentConditionDTO(supplier.getSID(), pc.getPaymentType().name(), pc.getBankAccount()));

        for (ContactPerson cp : supplier.getContactPersons()) {
            contactPersonDAO.insert(new ContactPersonDTO(supplier.getSID(), cp.getName(), cp.getPhone(), cp.getEmail()));
        }

        for (SupplierAgreement agreement : supplier.getAgreements()) {
            saveSupplierAgreement(agreement, supplier.getSID());

            for (ProductAgreement pa : agreement.getSupplierProductsDiscount()) {
                ProductAgreementDTO paDTO = new ProductAgreementDTO(
                        agreement.getAgreementID(),
                        pa.getProduct().getProductNum(),
                        pa.getMinQuantity(),
                        pa.getDiscount()
                );
                productAgreementDAO.insert(paDTO);
            }
        }

        suppliersCache.add(supplier);
    }

    @Override
    public void updateSupplier(Supplier supplier) throws SQLException {
        supplierDAO.update(new SupplierDTO(supplier.getSID(), supplier.getName(), supplier.getAddress()));
        supplyDayDAO.deleteAllForSupplier(supplier.getSID());
        for (DayOfWeek day : supplier.getSupplyDays()) {
            supplyDayDAO.insert(new SupplyDayDTO(supplier.getSID(), day.toString()));
        }

        paymentConditionDAO.delete(supplier.getSID());
        PaymentCondition pc = supplier.getPaymentCondition();
        paymentConditionDAO.insert(new PaymentConditionDTO(supplier.getSID(), pc.getPaymentType().name(), pc.getBankAccount()));

        contactPersonDAO.deleteAllForSupplier(supplier.getSID());
        for (ContactPerson cp : supplier.getContactPersons()) {
            contactPersonDAO.insert(new ContactPersonDTO(supplier.getSID(), cp.getName(), cp.getPhone(), cp.getEmail()));
        }

        List<SupplierAgreementDTO> existingAgreements = supplierAgreementDAO.getBySupplier(supplier.getSID());
        for (SupplierAgreementDTO agr : existingAgreements) {
            String agrID = agr.agreementID();
            for (SupplierAgreementProductDTO prod : agreementProductDAO.getByAgreement(agrID)) {
                agreementProductDAO.delete(agrID, prod.productSnum());
            }
            for (ProductAgreementDTO pa : productAgreementDAO.getByAgreement(agrID)) {
                productAgreementDAO.delete(agrID, pa.productSnum());
            }
            supplierAgreementDAO.delete(agrID);
        }

        for (SupplierAgreement agreement : supplier.getAgreements()) {
            saveSupplierAgreement(agreement, supplier.getSID());
            for (ProductAgreement pa : agreement.getSupplierProductsDiscount()) {
                ProductAgreementDTO paDTO = new ProductAgreementDTO(
                        agreement.getAgreementID(),
                        pa.getProduct().getProductNum(),
                        pa.getMinQuantity(),
                        pa.getDiscount()
                );
                productAgreementDAO.insert(paDTO);
            }
        }

        suppliersCache.removeIf(s -> s.getSID().equals(supplier.getSID()));
        suppliersCache.add(supplier);
    }

    @Override
    public void deleteSupplier(String sID) throws SQLException {
        supplyDayDAO.deleteAllForSupplier(sID);
        paymentConditionDAO.delete(sID);
        contactPersonDAO.deleteAllForSupplier(sID);

        List<SupplierAgreementDTO> agreements = supplierAgreementDAO.getBySupplier(sID);
        for (SupplierAgreementDTO agr : agreements) {
            String agrID = agr.agreementID();
            for (SupplierAgreementProductDTO prod : agreementProductDAO.getByAgreement(agrID)) {
                agreementProductDAO.delete(agrID, prod.productSnum());
            }
            for (ProductAgreementDTO pa : productAgreementDAO.getByAgreement(agrID)) {
                productAgreementDAO.delete(agrID, pa.productSnum());
            }
            supplierAgreementDAO.delete(agrID);
        }

        supplierDAO.delete(sID);

        suppliersCache.removeIf(s -> s.getSID().equals(sID));
    }

    @Override
    public Optional<Supplier> getSupplier(String sID) {
        return suppliersCache.stream()
                .filter(s -> s.getSID().equals(sID))
                .findFirst();
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliersCache);
    }

    @Override
    public void saveSupplierAgreement(SupplierAgreement agreement, String supplierId) throws SQLException {
        SupplierAgreementDTO agreementDTO = new SupplierAgreementDTO(
                agreement.getAgreementID(),
                supplierId,
                agreement.getContactPerson().getName(),
                agreement.hasRegularDays()
        );
        supplierAgreementDAO.insert(agreementDTO);

        for (SupplierProduct product : agreement.getProducts()) {
            SupplierAgreementProductDTO sapDTO = new SupplierAgreementProductDTO(
                    agreement.getAgreementID(),
                    product.getProductName(),
                    product.getProductNum(),
                    product.getPrice()
            );
            agreementProductDAO.insert(sapDTO);
        }

        getSupplier(supplierId).ifPresent(supplier -> supplier.getAgreements().add(agreement));
    }

    @Override
    public void deleteSupplierAgreement(SupplierAgreement agreement) throws SQLException {
        String agreementID = agreement.getAgreementID();
        for (SupplierProduct product : agreement.getProducts()) {
            agreementProductDAO.delete(agreementID, product.getProductNum());
        }
        for (ProductAgreement pa : agreement.getSupplierProductsDiscount()) {
            productAgreementDAO.delete(agreementID, pa.getProduct().getProductNum());
        }
        supplierAgreementDAO.delete(agreementID);

        for (Supplier supplier : suppliersCache) {
            supplier.getAgreements().removeIf(a -> a.getAgreementID().equals(agreementID));
        }
    }

    @Override
    public void saveContactPerson(ContactPerson person, String supplierId) throws SQLException {
        ContactPersonDTO dto = new ContactPersonDTO(supplierId, person.getName(), person.getPhone(), person.getEmail());
        contactPersonDAO.insert(dto);

        getSupplier(supplierId).ifPresent(supplier -> supplier.getContactPersons().add(person));
    }

    @Override
    public void addProductToAgreement(SupplierAgreement agreement, SupplierProduct product) throws SQLException {
        SupplierAgreementProductDTO dto = new SupplierAgreementProductDTO(
                agreement.getAgreementID(),
                product.getProductName(),
                product.getProductNum(),
                product.getPrice()
        );
        agreementProductDAO.insert(dto);

        agreement.getProducts().add(product);
    }

    @Override
    public void removeProductFromAgreement(SupplierAgreement agreement, String productNum) throws SQLException {
        agreementProductDAO.delete(agreement.getAgreementID(), productNum);
        agreement.getProducts().removeIf(p -> p.getProductNum().equals(productNum));
    }

    @Override
    public Optional<SupplierAgreement> getSupplierAgreement(String sID, String agreementId) {
        return suppliersCache.stream()
                .filter(supplier -> supplier.getSID().equals(sID))
                .findFirst()
                .flatMap(supplier -> supplier.getAgreements().stream()
                        .filter(agreement -> agreement.getAgreementID().equals(agreementId))
                        .findFirst());
    }
}
