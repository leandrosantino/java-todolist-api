package com.leandrosantino.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leandrosantino.todolist.Responses.HttpResponse;
import com.leandrosantino.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<HttpResponse<TaskModel>> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var response = new HttpResponse<TaskModel>();
        UUID idUser = (UUID) request.getAttribute("idUser");
        taskModel.setIdUser(idUser);

        var currentDate = LocalDateTime.now();

        if(currentDate.isAfter(taskModel.getStarAt())){
            response.setMessage("curent date is after of start date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        }

        if(currentDate.isAfter(taskModel.getEndAt())){
            response.setMessage("curent date is after of end date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        }

        if(taskModel.getStarAt().isAfter(taskModel.getEndAt())){
            response.setMessage("start date is after of end date ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        }



        var task = this.taskRepository.save(taskModel);
        response.setData(task);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = (UUID) request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser(idUser);

        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse<TaskModel>> update(
        @RequestBody TaskModel taskModel,
        @PathVariable UUID id,
        HttpServletRequest request
    ){
        var task = this.taskRepository.findById(id).orElse(null);
        var response = new HttpResponse<TaskModel>();

        if(task == null){
            response.setMessage("task not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        var idUser = (UUID) request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)){
            response.setMessage("this user not is the owner of task");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        response.setData(taskUpdated);
        return ResponseEntity.ok().body(response);
    }

}
