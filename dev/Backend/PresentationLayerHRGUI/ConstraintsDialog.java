package Backend.PresentationLayerHRGUI;

import Backend.DTO.TimeSlotDTO;
import Backend.ServiceLayer.ServiceLayerHR.EmloyeeService.EmployeeManageService;
import Backend.ServiceLayer.ServiceLayerHR.Response;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConstraintsDialog extends JDialog {
    private final EmployeeManageService service;
    private final DefaultListModel<TimeSlotDTO> model = new DefaultListModel<>();

    public ConstraintsDialog(EmployeeManageService service) {
        this.service = service;
        setTitle("Update Constraints");
        setModal(true);

        JList<TimeSlotDTO> list = new JList<>(model);
        JScrollPane scroll = new JScrollPane(list);

        JComboBox<Integer> cbDay = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7});
        JComboBox<Integer> cbShift = new JComboBox<>(new Integer[]{1,2});
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            TimeSlotDTO ts = new TimeSlotDTO((Integer)cbDay.getSelectedItem(),
                    (Integer)cbShift.getSelectedItem());
            model.addElement(ts);
        });

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            List<TimeSlotDTO> slots = new ArrayList<>();
            for (int i = 0; i < model.size(); i++) {
                slots.add(model.getElementAt(i));
            }
            Response r = service.updateConstraints(slots);
            JOptionPane.showMessageDialog(this,
                    r.getReturnValue() != null ? r.getReturnValue() : r.getErrorMsg());
            dispose();
        });

        JPanel controls = new JPanel();
        controls.add(new JLabel("Day:"));
        controls.add(cbDay);
        controls.add(new JLabel("Shift:"));
        controls.add(cbShift);
        controls.add(btnAdd);
        controls.add(btnSave);

        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(controls, BorderLayout.SOUTH);

        setSize(400,300);
        setLocationRelativeTo(null);
    }
}