package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DomainLayer.DomainLayerHR.*;
import Backend.DomainLayer.DomainLayerT.TransportFacade;

public class DataRepositoryImpl implements DataRepository {

    public void loadData() {
//        LoginFacade.getInstance().loadData();
        TransportFacade transportFacade = TransportFacade.getInstance();
        transportFacade.loadData();
        RolesFacade rolesFacade = RolesFacade.getInstance();
        rolesFacade.loadData();
        ConstraintsFacade constraintsFacade = ConstraintsFacade.getInstance();
        constraintsFacade.loadData();
        WeeklyShiftFacade weeklyShiftFacade = WeeklyShiftFacade.getInstance();
        weeklyShiftFacade.loadData();
        EmployeeFacade employeeFacade = EmployeeFacade.getInstance();
        employeeFacade.loadData();
        HRDL.getInstance().loadData();
    }
    

    public void deleteData() {
        TransportFacade.getInstance().deleteData();
        ConstraintsFacade.getInstance().deleteData();
        WeeklyShiftFacade.getInstance().deleteData();
        RolesFacade.getInstance().deleteData();
        EmployeeFacade.getInstance().deleteData();
        HRDL.getInstance().deleteData();
        LoginFacade.getInstance().deleteData();

    }

    public void loadLoginData() {
        LoginFacade loginFacade = LoginFacade.getInstance();
        loginFacade.loadData();
    }
}
