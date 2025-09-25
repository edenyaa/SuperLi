package Backend.DomainLayer.DomainLayerHR.Repos;

import Backend.DTO.EmployeeDTO;

public class ControllerFacade {
    private final ConstraintsRepository conRepo;
    private final HRInboxRepository HrinboxRepo;
    private final HRRepsitory  HrRepo;
    private final RoleRepository roleRepo;
    private final ShiftRepository shiftRepo;
    private final DataRepository dataRepo;



    public ControllerFacade() {
        this.conRepo = new ConstraintsRepoImpl();
        this.HrinboxRepo = new HRInboxRepoImpl();
        this.HrRepo = new HRRepImpl();
        this.roleRepo = new RoleRepoImpl();
        this.shiftRepo = new ShiftRepoImpl();
        this.dataRepo = new DataRepositoryImpl();
    }

    public void loadData() {
        try{
        dataRepo.loadData();
    }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteData() {
        try{
            dataRepo.deleteData();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void loadLoginData() {
        try{
            dataRepo.loadLoginData();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateBankAccount(String id, String bankAccount) {
        HrRepo.updateBankAccount(id, bankAccount);
    }

    public void editFullName(String id, String fullName) {
        HrRepo.updateFullName(id, fullName);
    }

    public void editPassword(String id, String password) {
        HrRepo.updatePassword(id, password);
    }

}
