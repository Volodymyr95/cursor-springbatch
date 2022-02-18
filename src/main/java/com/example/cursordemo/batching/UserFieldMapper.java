package com.example.cursordemo.batching;

import com.example.cursordemo.models.User;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class UserFieldMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {
        var user = new User();
        user.setId(fieldSet.readString("id"));
        user.setUsername(fieldSet.readString("username"));

        return user;
    }
}
