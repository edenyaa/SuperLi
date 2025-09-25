package dataAccessLayer.repository;

import dataAccessLayer.dao.*;
import dto.*;
import domainLayerInventory.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscountRepositoryImpl implements DiscountRepository {
    private final DiscountDAO discountDAO;
    private final DiscountItemDAO itemDAO;
    private final DiscountCategoryDAO categoryDAO;

    private final List<Discount> discounts = new ArrayList<>();
    private final List<DiscountItemDTO> discountItemDTOs = new ArrayList<>();
    private final List<DiscountCategoryDTO> discountCategoryDTOs = new ArrayList<>();

    public DiscountRepositoryImpl(DiscountDAO discountDAO, DiscountItemDAO itemDAO, DiscountCategoryDAO categoryDAO) throws SQLException {
        this.discountDAO = discountDAO;
        this.itemDAO = itemDAO;
        this.categoryDAO = categoryDAO;
        loadCache();
    }

    private void loadCache() throws SQLException {
        discounts.clear();
        discountItemDTOs.clear();
        discountCategoryDTOs.clear();

        List<DiscountDTO> allDTOs = discountDAO.getAll();

        Map<String, List<DiscountItemDTO>> allItemLinks = new HashMap<>();
        Map<String, List<DiscountCategoryDTO>> allCategoryLinks = new HashMap<>();

        for (DiscountDTO dto : allDTOs) {
            allItemLinks.put(dto.discountId(), itemDAO.getByDiscountId(dto.discountId()));
            allCategoryLinks.put(dto.discountId(), categoryDAO.getByDiscountId(dto.discountId()));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        for (DiscountDTO dto : allDTOs) {
            Discount.Status status = Discount.Status.valueOf(dto.appliesTo());
            List<String> barcodes = new ArrayList<>();
            Category category = null;

            if (status == Discount.Status.ITEM) {
                List<DiscountItemDTO> items = allItemLinks.getOrDefault(dto.discountId(), List.of());
                for (DiscountItemDTO item : items) {
                    barcodes.add(item.barcode());
                    discountItemDTOs.add(item);
                }
            } else if (status == Discount.Status.CATEGORY) {
                List<DiscountCategoryDTO> cats = allCategoryLinks.getOrDefault(dto.discountId(), List.of());
                if (!cats.isEmpty()) {
                    category = new Category(cats.get(0).categoryName());
                    discountCategoryDTOs.addAll(cats);
                }
            }

            try {
                Date startDate = sdf.parse(dto.startDate());
                Date endDate = sdf.parse(dto.endDate());

                Discount discount = new Discount(
                        dto.discountId(),
                        startDate,
                        endDate,
                        dto.percentage(),
                        dto.discountSetPrice(),
                        status,
                        barcodes,
                        category
                );

                discounts.add(discount);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format in discount " + dto.discountId(), e);
            }
        }
    }

    @Override
    public void saveDiscount(Discount discount) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        DiscountDTO dto = new DiscountDTO(
                discount.getId(),
                sdf.format(discount.getStartDate()),
                sdf.format(discount.getEndDate()),
                discount.getPercentage(),
                discount.getDiscountSetPrice(),
                discount.getAppliesTo().toString()
        );
        discountDAO.insert(dto);

        if (discount.getAppliesTo() == Discount.Status.ITEM) {
            for (String barcode : discount.getItemBarcodes()) {
                DiscountItemDTO itemDTO = new DiscountItemDTO(discount.getId(), barcode);
                itemDAO.insert(itemDTO);
                discountItemDTOs.add(itemDTO);
            }
        } else if (discount.getAppliesTo() == Discount.Status.CATEGORY) {
            DiscountCategoryDTO catDTO = new DiscountCategoryDTO(discount.getId(), discount.getItemsCategory().getName());
            categoryDAO.insert(catDTO);
            discountCategoryDTOs.add(catDTO);
        }

        discounts.add(discount);
    }

    @Override
    public void updateDiscount(Discount discount) throws SQLException {
        deleteDiscount(discount.getId());
        saveDiscount(discount);
    }

    @Override
    public void deleteDiscount(String discountId) throws SQLException {
        itemDAO.deleteByDiscountId(discountId);
        categoryDAO.deleteByDiscountId(discountId);
        discountDAO.delete(discountId);

        discounts.removeIf(d -> d.getId().equals(discountId));
        discountItemDTOs.removeIf(dto -> dto.discountId().equals(discountId));
        discountCategoryDTOs.removeIf(dto -> dto.discountId().equals(discountId));
    }

    @Override
    public Optional<Discount> getDiscount(String discountId) {
        return discounts.stream().filter(d -> d.getId().equals(discountId)).findFirst();
    }

    @Override
    public List<Discount> getDiscountsByProduct(String barcode) {
        List<Discount> results = new ArrayList<>();
        for (DiscountItemDTO item : discountItemDTOs) {
            if (item.barcode().equals(barcode)) {
                getDiscount(item.discountId()).ifPresent(results::add);
            }
        }
        return results;
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts);
    }

    public List<Discount> getDiscountsInMemory() {
        return discounts;
    }

    public List<DiscountItemDTO> getDiscountItemDTOsInMemory() {
        return discountItemDTOs;
    }

    public List<DiscountCategoryDTO> getDiscountCategoryDTOsInMemory() {
        return discountCategoryDTOs;
    }
}
