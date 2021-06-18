package com.example.auth.utility;

import com.example.generic.component.utility.MessageReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils
        extends MessageReader {

    @Autowired
    public MessageUtils(
            MessageSource messageSource) {
        super(messageSource);
    }

}
