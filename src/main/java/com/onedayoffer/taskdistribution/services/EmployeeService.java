package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {

        Optional<Sort> sort = buildSort(sortDirection, "fio");

        List<Employee> employees = sort.map(employeeRepository::findAllAndSort)
                .orElseGet(employeeRepository::findAll);

        Type type = new TypeToken<List<EmployeeDTO>>() {}.getType();

        return modelMapper.map(employees, type);
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден."));

        Type type = new TypeToken<EmployeeDTO>() {}.getType();

        return modelMapper.map(employee, type);
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        List<Task> tasks = taskRepository.findAllByEmployeeId(id);
        Type type = new TypeToken<List<TaskDTO>>() {}.getType();

        return modelMapper.map(tasks, type);
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Задача не найдена"));
        task.setStatus(status);
        taskRepository.save(task);
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Сотрудник не найден."));
        // todo: переделать маппинг на общий
        Task task = Task.builder()
                .name(newTask.getName())
                .employee(employee)
                .taskType(newTask.getTaskType())
                .status(newTask.getStatus())
                .priority(newTask.getPriority())
                .leadTime(newTask.getLeadTime())
                .build();

        taskRepository.save(task);
    }

    private Optional<Sort> buildSort(String sortDirection, String column) {
        if (StringUtils.isNotEmpty(sortDirection) && StringUtils.isNotEmpty(column)) {
            return Optional.of(Sort.by(Sort.Direction.fromString(sortDirection), column));
        }

        return Optional.empty();
    }

// todo: доработать маппинг для однострочного использования
//    private <T, V> List<T> map(List<V> entities, TypeToken<List<T>> typeToken) {
//        return modelMapper.map(entities, typeToken.getType());
//    }

}
