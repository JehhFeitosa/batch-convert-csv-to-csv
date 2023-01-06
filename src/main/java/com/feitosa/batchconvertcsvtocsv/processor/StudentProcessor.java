package com.feitosa.batchconvertcsvtocsv.processor;

import com.feitosa.batchconvertcsvtocsv.model.Student;

import javax.batch.api.chunk.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {

    @Override
    public Student process(Student item) throws Exception{
        final String firstName = item.getFirstName().toUpperCase();
        final String lastName = item.getLastName().toUpperCase();
        final Student data = new Student(item.getId(), firstName, lastName, item.getEmail());
        return data;

    }


}
