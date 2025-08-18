package com.consultadd.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TaskMappingId implements Serializable {
    private Long user;
    private Long task;

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof TaskMappingId))return false;
        TaskMappingId that=(TaskMappingId) o;
        return Objects.equals(user,that.user) && Objects.equals(task,that.task);
    }

    @Override
    public int hashCode(){
        return Objects.hash(user,task);
    }
}
