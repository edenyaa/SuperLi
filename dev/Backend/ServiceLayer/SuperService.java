package Backend.ServiceLayer;

import Backend.DomainLayer.DomainLayerHR.Repos.ControllerFacade;

public class SuperService {

    private final ControllerFacade controller;

    public SuperService() {
        this.controller = new ControllerFacade();
    }

    public void loadData() {
        controller.loadData();
    }

    public void deleteData() {
        controller.deleteData();
    }

    public void resetData() {
        deleteData();
        loadData();
    }

    public void loadLoginData() {
        controller.loadLoginData();
    }

    public ControllerFacade getController() {
        return controller;
    }
}
