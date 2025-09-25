package Backend.DomainLayer.DomainLayerT;

import Backend.DTO.*;
import Backend.DataAccessLayer.Controllers.AreaLocationDAOImpl;
import Backend.DataAccessLayer.Controllers.DeliveryDAOImpl;
import Backend.DataAccessLayer.Controllers.DocumentDAOImpl;
import Backend.DataAccessLayer.Controllers.TruckDAOImpl;

import java.util.AbstractMap.SimpleEntry;


import java.time.LocalDate;
import java.util.*;

import Exceptions.*;


public class TransportFacade {

    private static final TransportFacade instance = new TransportFacade();
    private HashMap<String, AreaDL> areaByName;
    private HashMap<Integer, DeliveryDL> deliveriesByID;
    private HashMap<Integer, TruckDL> trucksByID;
    private HashMap<Integer, DocumentDL> documentsByID;
    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


    private TransportFacade() {
        this.areaByName = new HashMap<>();
        this.deliveriesByID = new HashMap<>();
        this.trucksByID = new HashMap<>();
        this.documentsByID = new HashMap<>();
    }

    public static TransportFacade getInstance() {
        return instance;
    }

    //PRECONDITION: the area exists in the system
    //POSTCONDITION: the area is returned
    // If the area does not exist, an exception is thrown
    public AreaDTO getAreaByName(String name) {
        if(areaByName.containsKey(name)) {
            return new AreaDTO(areaByName.get(name));
        } else {
            throw new IllegalArgumentException("Area does not exist");
        }
    }
    //PRECONDITION: the delivery exists in the system
    //POSTCONDITION: the delivery is returned
    // If the delivery does not exist, an exception is thrown
    public DeliveryDTO getDeliveryByID(int id) {
        if(deliveriesByID.containsKey(id)) {
            return new DeliveryDTO(deliveriesByID.get(id));
        } else {
            throw new IllegalArgumentException("Delivery does not exist");
        }
    }
    //PRECONDITION: the truck exists in the system
    //POSTCONDITION: the truck is returned
    // If the truck does not exist, an exception is thrown
    public TruckDTO getTruckByID(int id) {
        if(trucksByID.containsKey(id)) {
            return new TruckDTO(trucksByID.get(id));
        } else {
            throw new IllegalArgumentException("Truck does not exist");
        }
    }

    //PRECONDITION: the location exists in the system, areaName and address are not null
    //POSTCONDITION: the location is returned
    // If the location does not exist, an exception is thrown

    public LocationDTO getLocationByAddress(String areaName,String address) {
        AreaDL area = areaByName.get(areaName);
        if (area != null) {
            LocationDL loc = null;
            for (LocationDL location : area.getLocations()) {
                if (location.getAddress().equals(address)) {
                    loc = location;
                    break;
                }
            }
            if(loc != null) {
                return new LocationDTO(loc);
            } else {
                throw new IllegalArgumentException("Location does not exist in the area");
            }
        }
        throw new IllegalArgumentException("Area does not exist");
    }
    //PRECONDITION: the document exists in the system
    //POSTCONDITION: the document is returned
    // If the document does not exist, an exception is thrown
    public DocumentDTO getDocumentByID(int id) {
        if(documentsByID.containsKey(id)) {
            return new DocumentDTO(documentsByID.get(id));
        } else {
            throw new IllegalArgumentException("Document does not exist");
        }
    }

    //PRECONDITION: the area does not exist in the system
    //POSTCONDITION: the area is added to the system
    // If the area already exists, an exception is thrown
    public void addArea(AreaDTO area) {
        if (area == null || area.getName() == null) {
            throw new IllegalArgumentException("Area or area name cannot be null");
        }
        if(areaByName.containsKey(area.getName())) {
            throw new IllegalArgumentException("Area already exists");
        }
        AreaLocationDAOImpl.getInstance().insert(area);
        areaByName.put(area.getName(), new AreaDL(area));
    }
    //PRECONDITION: the delivery does not exist in the system
    //POSTCONDITION: the delivery is added to the system
    // If the delivery already exists, an exception is thrown
    public void addDelivery(DeliveryDTO delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery cannot be null");
        }
        if(delivery.getId() < 0){
            throw new IllegalArgumentException("Delivery ID must be positive");
        }
        if (deliveriesByID.containsKey(delivery.getId())) {
            throw new IllegalArgumentException("Delivery already exists");
        }
        if(delivery.getListOfItems().isEmpty()) {
            throw new IllegalArgumentException("Delivery must have at least one item");
        }
        for (SimpleEntry<String, Integer> item : delivery.getListOfItems()) {
            if (item.getKey() == null || item.getValue() <= 0) {
                throw new IllegalArgumentException("Item name cannot be null and weight must be positive");
            }
        }
        DeliveryDAOImpl.getInstance().insert(delivery);
        deliveriesByID.put(delivery.getId(), new DeliveryDL(delivery));
    }
    //PRECONDITION: the truck does not exist in the system
    //POSTCONDITION: the truck is added to the system
    // If the truck already exists, an exception is thrown
    public void addTruck(TruckDTO truck) {
        if (truck == null || truck.getId() <= 0) {
            throw new IllegalArgumentException("Truck or truck ID cannot be null or negative");
        }        
        if (trucksByID.containsKey(truck.getId())) {
            throw new IllegalArgumentException("Truck already exists");
        }
        if (truck.getDryWeight() <= 0 || truck.getMaxLoad() <= 0 || truck.getWeight() < 0) {
            throw new IllegalArgumentException("Truck weight or max load cannot be negative or zero");
        }
        if (truck.getWeight() > truck.getMaxLoad()) {
            throw new IllegalArgumentException("Truck weight cannot be greater than max load");
        }
        if(truck.getDryWeight() > truck.getMaxLoad()) {
            throw new IllegalArgumentException("Truck dry weight cannot be greater than max load");
        }
        TruckDAOImpl.getInstance().insert(truck);
        trucksByID.put(truck.getId(), new TruckDL(truck));
    }

    //PRECONDITION: the location does not exist in the system, areaName and location are not null
    //POSTCONDITION: the location is added to the system
    // If the location already exists, an exception is thrown
    public void addLocation(String areaName, LocationDTO location) {
        if (location == null || areaName == null || location.getAddress() == null) {
            throw new IllegalArgumentException("Location, area name or address cannot be null");
        }
        if(areaByName.containsKey(areaName)) {
            LocationDL loc = new LocationDL(location);
            AreaDL area = areaByName.get(areaName);
            if (area.getLocations().contains(loc)) {
                throw new IllegalArgumentException("Location already exists in the area");
            }
            for (LocationDL existingLocation : area.getLocations()) {
                if (existingLocation.getAddress().equals(location.getAddress())) {
                    throw new IllegalArgumentException("Location with this address already exists in the area");
                }
            }
            AreaLocationDAOImpl.getInstance().insertLocationToArea(areaName, location);
            area.addLocation(loc);
        } else {
            throw new IllegalArgumentException("Area does not exist");
        }
    }
    
    //PRECONDITION: the document does not exist in the system
    //POSTCONDITION: the document is added to the system
    // If the document already exists, an exception is thrown
    public void addDocument(DocumentDTO document) {
        if (document == null || document.getId() <= 0) {
            throw new IllegalArgumentException("Document or document ID cannot be null or negative");
        }
        if (documentsByID.containsKey(document.getId())) {
            throw new IllegalArgumentException("Document already exists");
        }
        DocumentDAOImpl.getInstance().insert(document);
        documentsByID.put(document.getId(), new DocumentDL(document));
    }

    //PRECONDITION: the delivery exists in the system, the truck is not null and is available
    //POSTCONDITION: the delivery is assigned to the truck, a new document is created and added to the system
    //If preconditions are not met, an exception is thrown
    public void assignDeliveryToTruck(int deliveryID, int truckID) {
        if (truckID < 0) {
            throw new IllegalArgumentException("Truck ID cannot be negative");
        }
        TruckDL truck = trucksByID.get(truckID);
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery not found.");
        }
        int weight = delivery.calculateWeight();
        if(!truck.isAvailable()){
            throw new IllegalArgumentException("Truck is not available.");
            
        }else{ //truck is available
            if (truck.getWeight() + weight > truck.getMaxLoad()) { //no more room - reached max capacity
                throw new OverWeightException(truck.getId(), deliveryID, weight);
            }
            //else - truck is available and has enough room
            for (SimpleEntry<String, Integer> item : delivery.getListOfItems()) {
                truck.addItem(item);
            }
            truck.setWeight(truck.getWeight() + weight);
            if(truck.getWeight() == truck.getMaxLoad()){
                truck.markAsUnavailable();
            }
        }
        //reach here only if the truck can carry the weight of the delivery and all good.
        
        TruckDAOImpl.getInstance().update(new TruckDTO(truck));

        //Creating the document - reach here only if truck is assigned to the delivery
        DocumentDL document = documentsByID.get(deliveryID);
        if(document == null) { // if the document does not exist, create a new one
            document = new DocumentDL(delivery.getId(), delivery.getListOfItems(), 
            delivery.getOriginLoc(), delivery.getDestinationLoc(), delivery.getCreatedDate(), truck.getId(), null);
            DocumentDTO documentDTO = new DocumentDTO(document);
            documentsByID.put(document.getId(), document);
            DocumentDAOImpl.getInstance().insert(documentDTO);
        }
        else { // if the document already exists, update it
            document.setTruckID(truck.getId());
            DocumentDTO documentDTO = new DocumentDTO(document);
            documentsByID.put(document.getId(), document);
            DocumentDAOImpl.getInstance().update(documentDTO);
        }
    }
    //PRECONDITION: there are deliveries in the system
    //POSTCONDITION: the deliveries are sorted by time and returned
    // If there are no deliveries, an empty list is returned
    public LinkedList<DeliveryDTO> listDeliveriesByTime(int amount) {
        LinkedList<DeliveryDTO> deliveries = DeliveryDAOImpl.getInstance().getAll();
        deliveries.sort(Comparator.comparing(DeliveryDTO::getCreatedDate).reversed());
    
        int endIndex = Math.min(amount, deliveries.size());
        return new LinkedList<DeliveryDTO>(deliveries.subList(0, endIndex));
    }


    public void sendTruckToDistributeDeliveries(LinkedList<TransportationEmployeeDTO> employees, int truckID) {
        TruckDL truck = trucksByID.get(truckID);
        if (truck == null) {
            throw new IllegalArgumentException("Truck not found.");
        }
        LinkedList<DocumentDL> truckDocuments = new LinkedList<>();
        for (DocumentDTO document : getTruckDocuments(truckID)) {
            truckDocuments.add(new DocumentDL(document));
        }
        // first check if there a storage worker that is present in every location that the truck will pass
        // if there is no such worker, we will not send the truck
        LinkedList<LocationDL> destinations = new LinkedList<>();
        for (DocumentDL document : truckDocuments) {
            if (!destinations.contains(document.getDestination())) {
                destinations.add(document.getDestination());
            }
        }
        LocationDL locationStorageWorker = storageWorkerPresent(employees, destinations);

        if (locationStorageWorker != null) {
            throw new StorageWorkerNotPresentException(locationStorageWorker.getAddress(), locationStorageWorker.getAreaName());
        }

        // find driver with the required license and that is available
        TransportationEmployeeDTO driver = findAvailableDriver(employees, truck.getLicense());
        if (driver == null) {
            throw new NoDriverAvailableException(truck.getLicense());
        }

        // mark the driver as unavailable
        driver.setAvailable(false);
        // mark the truck as unavailable

        truck.markAsUnavailable();
        TruckDTO truckDTO = new TruckDTO(truck);
        TruckDAOImpl.getInstance().update(truckDTO);

        // update the documents with the name of the driver
        for (DocumentDL document : truckDocuments) {
            document.setDriverName(driver.getName());;
            document.setExitedTime(new Date());
            DocumentDTO documentDTO = new DocumentDTO(document);
            DocumentDAOImpl.getInstance().update(documentDTO);
        }
        

        for(DocumentDL document : truckDocuments) {
            deliveriesByID.remove(document.getId());
            DeliveryDTO deliveryDTO = new DeliveryDTO(document.getId(), document.getCreatedDate(), LocalDate.now(), new LocationDTO(document.getOrigin()), new LocationDTO(document.getDestination()), document.getListOfItems());
            DeliveryDAOImpl.getInstance().delete(deliveryDTO);
        }
    }

    // returns a driver with the required license and that is available, and that work at 
    // the given shift, if there is no such driver, the function will return null
    public TransportationEmployeeDTO findAvailableDriver(LinkedList<TransportationEmployeeDTO> employees, String licenseType) {
        for (TransportationEmployeeDTO driver : employees) {
            if (driver.getLicenseTypes().contains(licenseType) && driver.getAvailable()) {
                return driver;
            }
        }
        return null;
    }

    // check if there is a storage worker in every location that the truck will pass
    // if there is no such worker, the function will return null
    // if there is such worker, the function will return the location of the worker
    public LocationDL storageWorkerPresent(LinkedList<TransportationEmployeeDTO> employees, LinkedList<LocationDL> locations) {
        // finding all the storage workers (transportation employees, that are not drivers) 
        Set<Integer> storageWorkers = new HashSet<>();
        for (TransportationEmployeeDTO storageWorker : employees) {
            if(storageWorker.getLicenseTypes().isEmpty()){
                storageWorkers.add(storageWorker.getBranch().getId());
            }
        }
        for (LocationDL location : locations) {
            if (!storageWorkers.contains(location.getId())) {
                return location;
            }
        }
        return null;
    }

    //PRECONDITION: null
    //POSTCONDITION: a list of all documents of the given truck is returned
    // If the truck does not exist or doesnt have any documents related to it, an empty list is returned
    public LinkedList<DocumentDTO> getTruckDocuments(int truckID) {
        TruckDL truck = trucksByID.get(truckID);
        if (truck == null) {
            throw new IllegalArgumentException("Truck not found.");
        }
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        for (DocumentDL document : documentsByID.values()) {
            if (document.getTruckID() == truckID) {
                documents.add(new DocumentDTO(document));
            }
        }
        return documents;
    }

    // PRECONDITION: the delivery exists in the system, the area and destination exists in the system
    // POSTCONDITION: the delivery's destination is changed to the new location
    // If the delivery or area does not exist, an exception is thrown
    public void changeDestination(String areaName, int deliveryID, String destination) {
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        if (delivery != null) {
            AreaDL area = areaByName.get(areaName);
            if (area == null) {
                throw new IllegalArgumentException("Area not found.");
            }
            LocationDL newDestination = null;
            for(LocationDL location : area.getLocations()){
                if(location.getAddress().equals(destination)){
                    newDestination = location;
                    break;
                }
            }            
            if (newDestination != null) {
                delivery.setDestinationLoc(newDestination);
                DocumentDL document = documentsByID.get(deliveryID);
                if (document != null) {
                    DocumentDTO documentDTO = new DocumentDTO(document);
                    document.setDestination(newDestination);
                    DocumentDAOImpl.getInstance().update(documentDTO);
                } 
            }
            else {
                throw new IllegalArgumentException("New destination not found.");
            }
        }
        else {
            throw new IllegalArgumentException("Delivery not found.");
        }
        DeliveryDAOImpl.getInstance().update(new DeliveryDTO(delivery));
    }

    // PRECONDITION: the delivery exists in the system
    // POSTCONDITION: the delivery is cancelled, the items are removed from the truck and the document is removed from the system
    // If the delivery does not exist, an exception is thrown
    public void cancelDelivery(int deliveryID) {
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        DocumentDL document = documentsByID.get(deliveryID);
        
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery does not exist");
        }
        else if (document != null) {
            DeliveryDTO deliveryDTO = new DeliveryDTO(delivery);
            DocumentDTO documentDTO = new DocumentDTO(document);
            int truckID = documentDTO.getTruckID();
            LinkedList<SimpleEntry<String, Integer>> items = documentDTO.getListOfItems();
            deliveriesByID.remove(deliveryID);
            documentsByID.remove(deliveryID);

            for(TruckDL truck : trucksByID.values()) {
                if(truck.getId() == truckID) {
                    for(SimpleEntry<String, Integer> item : items) {
                        truck.removeItem(item.getKey());
                    }
                    truck.setWeight(truck.getWeight() - document.calculateWeight());
                    TruckDTO truckDTO = new TruckDTO(truck);
                    TruckDAOImpl.getInstance().update(truckDTO);
                    break;
                }
            }
            DeliveryDAOImpl.getInstance().delete(deliveryDTO);
            DocumentDAOImpl.getInstance().delete(documentDTO);
        }
        else{
            DeliveryDTO deliveryDTO = new DeliveryDTO(delivery);
            deliveriesByID.remove(deliveryID);
            DeliveryDAOImpl.getInstance().delete(deliveryDTO);
        }
    }

    // PRECONDITION: the document exists in the system
    // POSTCONDITION: the document is updated with the new items and the items to remove are removed from the truck
    // If the document does not exist, an exception is thrown
    public void updateDocument(int documentID, LinkedList<SimpleEntry<String, Integer>> newItems, LinkedList<SimpleEntry<String, Integer>> itemsToRemove) {
        DocumentDL document = documentsByID.get(documentID);
        if(document == null) {
            throw new IllegalArgumentException("Document not found.");
        }
        for(SimpleEntry<String, Integer> item : itemsToRemove){
            document.removeItem(item.getKey());
        }
        for(SimpleEntry<String, Integer> item : newItems){
            document.addItem(item);
        }
        DocumentDTO documentDTO = new DocumentDTO(document);
        DocumentDAOImpl.getInstance().update(documentDTO);


        TruckDL truck = trucksByID.get(document.getTruckID());
        int weight = 0;
        for (SimpleEntry<String, Integer> item : itemsToRemove) {
            truck.removeItem(item.getKey());
            weight -= item.getValue();
        }
        for(SimpleEntry<String, Integer> item : newItems) {
            truck.addItem(item);
            weight += item.getValue();
            if(truck.getWeight() + weight > truck.getMaxLoad()) {
                throw new OverWeightException(truck.getId(), documentID, weight);
            }
        }
        truck.setWeight(truck.getWeight() + weight);
        if(truck.getWeight() == truck.getDryWeight()) {
            truck.markAsAvailable();
        }
        TruckDTO truckDTO = new TruckDTO(truck);
        TruckDAOImpl.getInstance().update(truckDTO);


    }

    public void changeTruck(int deliveryID, int newTruck) {
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        DocumentDL document = documentsByID.get(deliveryID);
        TruckDL truck = trucksByID.get(newTruck);
        if (delivery != null) {
            if(truck != null) {
                if(document == null){
                    assignDeliveryToTruck(deliveryID, newTruck);
                }else{
                    if(newTruck == document.getTruckID()) {
                        throw new IllegalArgumentException("Delivery is already assigned to this truck.");
                    }
                    documentsByID.remove(deliveryID);
                    DocumentDAOImpl.getInstance().delete(new DocumentDTO(document));
                    TruckDL oldTruck = trucksByID.get(document.getTruckID());
                    oldTruck.removeItems(document.getListOfItems());
                    oldTruck.setWeight(oldTruck.getWeight() - document.calculateWeight());
                    TruckDAOImpl.getInstance().update(new TruckDTO(oldTruck));
                    assignDeliveryToTruck(deliveryID, newTruck);
                }
            }
            else {
                throw new IllegalArgumentException("Truck not found.");
            }
        }
        else {
            throw new IllegalArgumentException("Delivery not found.");
        }
    }

    // PRECONDITION: the delivery exists in the system
    // POSTCONDITION: the items are removed from the delivery
    // If the delivery does not exist, an exception is thrown
    public void removeItemsFromDelivery(int deliveryID, LinkedList<SimpleEntry<String, Integer>> items) {
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        if (delivery != null) {
            for (SimpleEntry<String, Integer> item : items) {
                delivery.removeItem(item.getKey(), item.getValue()); 
            }
    
            if (delivery.getListOfItems().isEmpty()) {
                deliveriesByID.remove(deliveryID);
                DeliveryDAOImpl.getInstance().delete(new DeliveryDTO(delivery));
            } else {
                DeliveryDTO deliveryDTO = new DeliveryDTO(delivery);
                DeliveryDAOImpl.getInstance().update(deliveryDTO);
            }
        } else {
            throw new IllegalArgumentException("Delivery not found.");
        }
    }
    

    // PRECONDITION: the truck exists in the system, the delivery exists in the system
    // POSTCONDITION: the items are removed from the truck and the delivery
    // If the truck or delivery does not exist, an exception is thrown
    public void removeItemsFromTruck(int truckID, int deliveryID, LinkedList<SimpleEntry<String, Integer>> items) {
        TruckDL truck = trucksByID.get(truckID);
        int weight = 0;
        if (truck != null) {
            for (SimpleEntry<String, Integer> item : items) {
                if (!truck.removeItem(item.getKey())) {
                    throw new IllegalArgumentException("Item not found in truck.");
                }
                weight += item.getValue();
            }
            truck.setWeight(truck.getWeight() - weight);
            if(truck.getWeight() == truck.getDryWeight()) {
                truck.markAsAvailable();
            }
            TruckDAOImpl.getInstance().update(new TruckDTO(truck));
            DocumentDL document = documentsByID.get(deliveryID);
            if (document != null) {
                for (SimpleEntry<String, Integer> item : items) {
                    document.removeItem(item.getKey());
                }
                if(document.getListOfItems().isEmpty()) {
                    documentsByID.remove(deliveryID);
                    DocumentDAOImpl.getInstance().delete(new DocumentDTO(document));
                } else {
                    DocumentDTO documentDTO = new DocumentDTO(document);
                    DocumentDAOImpl.getInstance().update(documentDTO);
                }
            } else {
                throw new IllegalArgumentException("Document not found.");
            }
        }else {
            throw new IllegalArgumentException("Truck not found.");
        }
    }

    // PRECONDITION: null
    // POSTCONDITION: a list of all available trucks is returned
    // If there are no available trucks, an empty list is returned
    public LinkedList<TruckDTO> getAvailableTrucks() {
        LinkedList<TruckDTO> availableTrucks = new LinkedList<>();
        for (TruckDL truck : trucksByID.values()) {
            if (truck.isAvailable()) {
                availableTrucks.add(new TruckDTO(truck));
            }
        }
        return availableTrucks;
    }

    // PRECONDITION: the truck exists in the system
    // POSTCONDITION: the truck is deleted from the system
    // If the truck does not exist, an exception is thrown
    public void deleteTruck(int truckID) {
        TruckDL truck = trucksByID.get(truckID);
        if (truck == null) {
            throw new IllegalArgumentException("Truck does not exist");
        }
        for (DocumentDL document : documentsByID.values()) {
            if (document.getTruckID() == truckID) {
                document.setTruckID(-1);
                DocumentDAOImpl.getInstance().update(new DocumentDTO(document));
            }
        }
        trucksByID.remove(truckID);
        TruckDAOImpl.getInstance().delete(new TruckDTO(truck));
    }
    // PRECONDITION: the location and area exists in the system
    // POSTCONDITION: the location is deleted from the system and removed from the area
    // If the location or area does not exist, an exception is thrown
    // If there are documents or deliveries related to the location, they are both deleted from the system
    public void deleteLocation(String areaName, String locationAddress) {
        AreaDL area = areaByName.get(areaName);
        if (area == null) {
            throw new IllegalArgumentException("No area with this location exists");
        }
        LocationDL location = area.getLocationByAddress(locationAddress);
        if (location == null) {
            throw new IllegalArgumentException("Location does not exist in the area");
        }
        for (DocumentDL document : documentsByID.values()) {
            if (document.getOrigin().equals(location) || document.getDestination().equals(location)) {
                TruckDL truck = trucksByID.get(document.getTruckID());
                if (truck != null) {
                    for (SimpleEntry<String, Integer> item : document.getListOfItems()) {
                        truck.removeItem(item.getKey());
                    }
                    truck.setWeight(truck.getWeight() - document.calculateWeight());
                    if (truck.getWeight() == truck.getDryWeight()) {
                        truck.markAsAvailable();
                    }
                    TruckDAOImpl.getInstance().update(new TruckDTO(truck));
                }
                documentsByID.remove(document.getId());
                DocumentDAOImpl.getInstance().delete(new DocumentDTO(document));
            }
        }
        for (DeliveryDL delivery : deliveriesByID.values()) {
            if (delivery.getOriginLoc().equals(location) || delivery.getDestinationLoc().equals(location)) {
                deliveriesByID.remove(delivery.getId());
                DeliveryDAOImpl.getInstance().delete(new DeliveryDTO(delivery));
            }
        }
        area.removeLocation(location);
        AreaLocationDAOImpl.getInstance().deleteLocationFromArea(areaName, new LocationDTO(location));
    }
    // PRECONDITION: the area exists in the system
    // POSTCONDITION: the area is deleted from the system
    // If the area does not exist, an exception is thrown
    // If there are locations related to the area, they are deleted using the deleteLocation method
    public void deleteArea(String areaName) {
        AreaDL area = areaByName.get(areaName);
        if (area == null) {
            throw new IllegalArgumentException("Area does not exist");
        }
        for (LocationDL location : area.getLocations()) {
            deleteLocation(areaName, location.getAddress());
            AreaLocationDAOImpl.getInstance().deleteLocationFromArea(areaName, new LocationDTO(location));
        }
        areaByName.remove(areaName);
        AreaLocationDAOImpl.getInstance().delete(new AreaDTO(area));
    }
    // PRECONDITION: the delivery exists in the system
    // POSTCONDITION: the delivery is marked as completed, the items are removed from the truck and the document is removed from the system
    // If the delivery does not exist, an exception is thrown
    public DocumentDTO supplierPickUpItems(int deliveryID) {
        DeliveryDL delivery = deliveriesByID.get(deliveryID);
        if (delivery == null) {
            throw new IllegalArgumentException("Delivery does not exist");
        }

        DocumentDL oldDoc = documentsByID.get(deliveryID);
        if (oldDoc != null) {
            TruckDL truck = trucksByID.get(oldDoc.getTruckID());
            for (SimpleEntry<String, Integer> item : oldDoc.getListOfItems()) {
                truck.removeItem(item.getKey());
            }
            truck.setWeight(truck.getWeight() - delivery.calculateWeight());
            if (truck.getWeight() == truck.getDryWeight()) {
                truck.markAsAvailable();
            }
            documentsByID.remove(deliveryID);
            DocumentDAOImpl.getInstance().delete(new DocumentDTO(oldDoc));
        }
    
        DocumentDL document = new DocumentDL(deliveryID, delivery.getListOfItems(), delivery.getOriginLoc(), delivery.getDestinationLoc(), delivery.getCreatedDate(), -1, "Supplier");
        document.setExitedTime(new Date());
        DocumentDAOImpl.getInstance().insert(new DocumentDTO(document));
        deliveriesByID.remove(deliveryID);
        DeliveryDAOImpl.getInstance().delete(new DeliveryDTO(delivery));
        return new DocumentDTO(document);
    }
    
    // PRECONDITION: the document exists in the system, the area and location exist in the system
    // POSTCONDITION: the document's origin is changed to the new location
    // If the document, area or location does not exist, an exception is thrown
    public void changeDocuementOrigin(int documentID, String areaName, String locationName) {
        DocumentDL document = documentsByID.get(documentID);
        LocationDL location = null;
        if (document == null) {
            throw new IllegalArgumentException("Document does not exist");
        }
        AreaDL area = areaByName.get(areaName);
        if (area == null) {
            throw new IllegalArgumentException("Area does not exist");
        }
        for(LocationDL loc : area.getLocations()){
            if(loc.getAddress().equals(locationName)){
                location = loc;
                break;
            }
        }
        if(location == null){
            throw new IllegalArgumentException("Location does not exist");
        }
        document.setOrigin(location);
        DocumentDAOImpl.getInstance().update(new DocumentDTO(document));
    }
    // PRECONDITION: the document exists in the system, the area and location exist in the system
    // POSTCONDITION: the document's destination is changed to the new location
    // If the document, area or location does not exist, an exception is thrown
	public void changeDocuementDestination(int documentID, String areaName, String locationName) {
        DocumentDL document = documentsByID.get(documentID);
        LocationDL location = null;
        if (document == null) {
            throw new IllegalArgumentException("Document does not exist");
        }
        AreaDL area = areaByName.get(areaName);
        if (area == null) {
            throw new IllegalArgumentException("Area does not exist");
        }
        for(LocationDL loc : area.getLocations()){
            if(loc.getAddress().equals(locationName)){
                location = loc;
                break;
            }
        }
        if(location == null){
            throw new IllegalArgumentException("Location does not exist");
        }
        document.setDestination(location);
        DocumentDAOImpl.getInstance().update(new DocumentDTO(document));
    }

    public LinkedList<AreaDTO> getAllAreas() {
        LinkedList<AreaDTO> areas = new LinkedList<>();
        for (AreaDL area : areaByName.values()) {
            areas.add(new AreaDTO(area));
        }
        return areas;
    }

    public boolean isValidDay(String day) {
        for (String d : days) {
            if (d.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }

    public int getDayNum(String dayName) {
        switch (dayName.toUpperCase()) {
            case "SUNDAY":
                return 1;
            case "MONDAY":
                return 2;
            case "TUESDAY":
                return 3;
            case "WEDNESDAY":
                return 4;
            case "THURSDAY":
                return 5;
            case "FRIDAY":
                return 6;
            case "SATURDAY":
                return 7;
            default:
                throw new IllegalArgumentException("Invalid day of the week");
        }
    }

    public int getShift(int hour) {
        return hour < 14 ? 1 : 2;
    }

    public boolean isValidShift(int shift) {
        return shift >= 8 || shift <= 20;
    }

    public void loadData() {
        LinkedList<AreaDTO> areaDTOs = AreaLocationDAOImpl.getInstance().getAll();
        for (AreaDTO areaDTO : areaDTOs) {
            AreaDL area = new AreaDL(areaDTO);
            areaByName.put(area.getName(), area);
        }
        LinkedList<DeliveryDTO> deliveryDTOs = DeliveryDAOImpl.getInstance().getAll();
        for (DeliveryDTO deliveryDTO : deliveryDTOs) {
            DeliveryDL delivery = new DeliveryDL(deliveryDTO);
            deliveriesByID.put(delivery.getId(), delivery);
        }
        LinkedList<TruckDTO> truckDTOs = TruckDAOImpl.getInstance().getAll();
        for (TruckDTO truckDTO : truckDTOs) {
            TruckDL truck = new TruckDL(truckDTO);
            trucksByID.put(truck.getId(), truck);
        }
        LinkedList<DocumentDTO> documentDTOs = DocumentDAOImpl.getInstance().getAll();
        for (DocumentDTO documentDTO : documentDTOs) {
            DocumentDL document = new DocumentDL(documentDTO);
            documentsByID.put(document.getId(), document);
        }

    }

    public void deleteData() {
        areaByName.clear();
        deliveriesByID.clear();
        trucksByID.clear();
        documentsByID.clear();
        AreaLocationDAOImpl.getInstance().deleteAll();
        DeliveryDAOImpl.getInstance().deleteAll();
        TruckDAOImpl.getInstance().deleteAll();
        DocumentDAOImpl.getInstance().deleteAll();
    }

    public int getMaxDeliveryID() {
        return DeliveryDAOImpl.getInstance().getMaxID();
    }

    public int getMaxTruckID() {
        return TruckDAOImpl.getInstance().getMaxID();
    }

    public int getMaxDocumentID() {
        return DocumentDAOImpl.getInstance().getMaxID();
    }

    public int getMaxLocationID() {
        return AreaLocationDAOImpl.getInstance().getMaxID();
    }

    public LinkedList<DeliveryDTO> getAllDeliveries() {
        LinkedList<DeliveryDTO> deliveries = new LinkedList<>();
        for (DeliveryDL delivery : deliveriesByID.values()) {
            deliveries.add(new DeliveryDTO(delivery));
        }
        return deliveries;
    }

    public LinkedList<DeliveryDTO> getNotAssignedDeliveries(){
        LinkedList<DeliveryDTO> notAssignedDeliveries = new LinkedList<>();
        for (DeliveryDL delivery : deliveriesByID.values()) {
            DocumentDL document = documentsByID.get(delivery.getId());
            if(document == null || (document.getTruckID() == -1 && document.getDriverName() == null)) {
                notAssignedDeliveries.add(new DeliveryDTO(delivery));
            }
        }
        return notAssignedDeliveries;
    }

    public LinkedList<DeliveryDTO> getAssignedDeliveries() {
        LinkedList<DeliveryDTO> assignedDeliveries = new LinkedList<>();
        for (DeliveryDL delivery : deliveriesByID.values()) {
            DocumentDL document = documentsByID.get(delivery.getId());
            if(document != null && document.getTruckID() != -1 && document.getDriverName() == null){
                assignedDeliveries.add(new DeliveryDTO(delivery));
            }
        }
        return assignedDeliveries;
    }

    public LinkedList<TruckDTO> getAllTrucks() {
        LinkedList<TruckDTO> trucks = new LinkedList<>();
        for (TruckDL truck : trucksByID.values()) {
            trucks.add(new TruckDTO(truck));
        }
        return trucks;
    }

    public LinkedList<DocumentDTO> getAllDocuments() {
        LinkedList<DocumentDTO> documents = new LinkedList<>();
        for (DocumentDL document : documentsByID.values()) {
            documents.add(new DocumentDTO(document));
        }
        return documents;
        
    }
}