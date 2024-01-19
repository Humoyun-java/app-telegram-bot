package org.example.service;

import org.example.model.Language;
import org.example.model.UserDto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class Keyboard {

    public static void userKeyboard(UserDto dto, SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        KeyboardRow row1, row2 = null, row3, row4;

        KeyboardButton button1, button2, button3, button4;

        switch (dto.getStep()) {
            case WELCOME:
                row1 = new KeyboardRow();
                button1 = new KeyboardButton("O'zbek Tili");
                button2 = new KeyboardButton("Русский Язык");
                row1.add(button1);
                row2.add(button2);
            case HOME:
                if (dto.getLanguage().equals(Language.UZB)) {
                    row1 = new KeyboardRow();
                    button1 = new KeyboardButton("Bayram 1");

                    row2 = new KeyboardRow();
                    button2 = new KeyboardButton("Bayram 2");

                    row3 = new KeyboardRow();
                    button3 = new KeyboardButton("Bayram 3");

                    row4 = new KeyboardRow();
                    button4 = new KeyboardButton("Bayram 4");

                    row1.add(button1);
                    row2.add(button2);
                    row3.add(button3);
                    row4.add(button4);
                }
        }
        keyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(keyboardMarkup);


    }
}
