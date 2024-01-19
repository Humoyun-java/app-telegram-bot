package org.example.service;

import org.example.model.Holiday;
import org.example.model.Language;
import org.example.model.Step;
import org.example.model.UserDto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;


public class MyBot extends TelegramLongPollingBot {
    Map<Long, UserDto> users = new HashMap<>();
    SendMessage sendMessage = new SendMessage();
    Long chatId;
    UserDto userDto = users.get(chatId);
    String text;

    @Override
    public void onUpdateReceived(Update update) {

        if (Objects.nonNull(update.getMessage())) {
            chatId = update.getMessage().getChatId();
//            System.out.println(chatId + "    from 1");
        } else {
            update.getCallbackQuery().getMessage().getChat().getId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
//            System.out.println(chatId + "    from 2 ");
        }
//        System.out.println(chatId);

        if (!users.containsKey(chatId)) {
            userDto = new UserDto(Language.NULL, Step.WELCOME, Holiday.NULL);
            users.put(update.getMessage().getChatId(), userDto);
        } else if (Objects.nonNull(update.getMessage()) && Objects.nonNull(update.getMessage().getText())) {
            if (update.getMessage().getText().equals("/start")) {
                userDto = new UserDto(Language.NULL, Step.WELCOME, Holiday.NULL);

            }

        }

        switch (users.get(chatId).getStep()) {
            case WELCOME: {
                Message message = update.getMessage();
                sendLanguageOptions(message);

                userDto.setStep(Step.LANGUAGE);
                userDto.setLanguage(Language.NULL);
                userDto.setHoliday(Holiday.NULL);

                users.replace(update.getMessage().getChatId(), userDto);
                break;
            }

            case LANGUAGE: {
                String callBackData = update.getCallbackQuery().getData();

                if (callBackData.equals("UZ_BUTTON")) {
                    text = "Assalomu Aleykum! Bu Bot Siz Uchun Tabriknoma Yasab Beradi!";
                    sendMessage.setText(text);
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setLanguage(Language.UZB);
                    userDto.setStep(Step.CHECK_HOLIDAY);


                    users.replace(chatId, userDto);
                }
                if (callBackData.equals("RU_BUTTON")) {
                    text = "Здраствуйте! Этот Бот Создаёт Для Вас Поздравительные Открытки!";
                    sendMessage.setText(text);
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setStep(Step.CHECK_HOLIDAY);
                    userDto.setLanguage(Language.RUS);


                    users.replace(chatId, userDto);
                }
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                sendHomeMessage(chatId, userDto.getLanguage());
                break;
            }

            case CHECK_HOLIDAY: {
                sendMessage = new SendMessage();
                String callBackData = update.getCallbackQuery().getData();

                if (callBackData.equals("VALENTINE_BUTTON")) {
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setHoliday(Holiday.VALENTINE);
                    userDto.setStep(Step.NAME);
                    userDto.setLanguage(Language.UZB);

                    users.replace(chatId, userDto);
                }
                if (callBackData.equals("NEW_YEAR_BUTTON")) {
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setStep(Step.NAME);
                    userDto.setHoliday(Holiday.NEW_YEAR);
                    userDto.setLanguage(Language.RUS);

                    users.replace(chatId, userDto);
                }
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }


            case NAME: {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                String imagePath = writeToImage(update.getMessage().getText(), userDto.getHoliday());

                if (userDto.getLanguage().equals(Language.UZB)) {
                    sendPhoto.setPhoto(new InputFile(new File(imagePath)));

                    //                        goHome(userDto.getLanguage());
                    userDto.setStep(Step.NAME);
                    users.replace(chatId, userDto);
                }

                if (userDto.getLanguage().equals(Language.RUS)) {
                    sendPhoto.setPhoto(new InputFile(new File(imagePath)));

                    //                        goHome(userDto.getLanguage());
                    userDto.setStep(Step.NAME);
                    users.replace(chatId, userDto);
                }

                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            }

            case CHECK_HOME: {
                SendMessage sendMessage1 = new SendMessage();
                String callBackData = update.getCallbackQuery().getData();

                if (callBackData.equals("YES_BUTTON")) {
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setStep(Step.HOME);

                    users.replace(chatId, userDto);
                }
                if (callBackData.equals("NO_BUTTON")) {
                    sendMessage.setChatId(String.valueOf(chatId));

                    userDto.setStep(Step.NAME);

                    users.replace(chatId, userDto);

                }
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                if (userDto.getLanguage().equals(Language.UZB)) {
                    sendMessage1.setText("Yangi Ism Kiriting");
                }
                if (userDto.getLanguage().equals(Language.RUS)) {
                    sendMessage1.setText("Введите Новое Имя");
                }

                sendMessage1.setChatId(String.valueOf(chatId));
                try {
                    execute(sendMessage1);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "year_new_test_bot";
    }

    @Override
    public String getBotToken() {
        return "6820158244:AAGsZf8DE6kVuew72Mb0OH-Gh51Niv5ezvo";
    }

    public String writeToImage(String name, Holiday holiday) {
        String imagePath = "";
        File outputImageFile = new File("");


        if (holiday.equals(Holiday.VALENTINE)) {
            imagePath = "D:\\Java\\app-image-generation\\src\\main\\resources\\Valentine.bmp";
        }

        if (holiday.equals(Holiday.NEW_YEAR)) {
            imagePath = "D:\\Java\\app-image-generation\\src\\main\\resources\\image.bmp";
        }

        try {
            File file = new File(imagePath);
            BufferedImage image = ImageIO.read(file);
            Graphics2D graphics = image.createGraphics();

            Font font = new Font("Times New Roman", Font.ITALIC, 100);
            graphics.setFont(font);
            graphics.setColor(Color.RED);
            FontMetrics fontMetrics = graphics.getFontMetrics();

            int textWidth = fontMetrics.stringWidth(name);
            int textHeight = fontMetrics.getHeight();

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            int x = (imageWidth - textWidth) / 2;
            int y = imageHeight - textHeight;

            graphics.drawString(name, x, y);

            graphics.dispose();

            if (holiday.equals(Holiday.VALENTINE)) {
                outputImageFile = new File("D:\\Java\\app-image-generation\\src\\main\\resources\\created\\ValentineCreated" + UUID.randomUUID() + ".bmp");
            }

            if (holiday.equals(Holiday.NEW_YEAR)) {
                outputImageFile = new File("D:\\Java\\app-image-generation\\src\\main\\resources\\created\\NewYearCreated" + UUID.randomUUID() + ".bmp");
            }

            ImageIO.write(image, "bmp", outputImageFile);
            System.out.println("Text has been written onto the image.");
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return outputImageFile.getPath();
    }

    public void sendLanguageOptions(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Tilni tanlang | Выберите Язык:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();


        // Option 1: O'zbek Tili
        InlineKeyboardButton uzbek = new InlineKeyboardButton();
        uzbek.setText("O'zbek Tili \uD83C\uDDFA\uD83C\uDDFF");
        uzbek.setCallbackData("UZ_BUTTON");
        rowInline.add(uzbek);


        // Option 2: Русский Язык
        InlineKeyboardButton russian = new InlineKeyboardButton();
        russian.setText("Русский Язый \uD83C\uDDF7\uD83C\uDDFA");
        russian.setCallbackData("RU_BUTTON");
        rowInline.add(russian);


        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendHomeMessage(Long chatId, Language language) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        SendMessage sendMessage = new SendMessage();

        if (language.equals(Language.UZB)) text = "Bayramlardan Birini Tanlang";
        else if (language.equals(Language.RUS)) text = "Выберите Одтн Из Празднтков";

        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);

        // Option 1: Valentin kuni
        InlineKeyboardButton valentine = new InlineKeyboardButton();
        if (language.equals(Language.UZB)) {
            valentine.setText("Valentin Kuni ❤\uFE0F");

        }
        if (language.equals(Language.RUS)) {
            valentine.setText("День Святого Валентина ❤\uFE0F");
        }
        valentine.setCallbackData("VALENTINE_BUTTON");
        rowInline1.add(valentine);

        // Option 2: Yangi Yil
        InlineKeyboardButton newYear = new InlineKeyboardButton();
        if (language.equals(Language.UZB)) {
            newYear.setText("Yangi Yil ☃\uFE0F");
        }
        if (language.equals(Language.RUS)) {
            newYear.setText("Новый Год ☃\uFE0F");
        }
        newYear.setCallbackData("NEW_YEAR_BUTTON");
        rowInline2.add(newYear);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void goHome(Language language) throws TelegramApiException {
        sendMessage.setChatId(String.valueOf(chatId));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        var noButton = new InlineKeyboardButton();
        sendMessage.setText("Bosh Menyuga Qaytishni Hohlaysizmi?");

        if (language.equals(Language.UZB)) {
            sendMessage.setText("Bosh Menyuga Qaytishni Hohlaysizmi?");

            yesButton.setText("Ha");
            yesButton.setCallbackData("YES_BUTTON");

            noButton.setText("Yo'q");
            noButton.setCallbackData("NO_BUTTON");
        }

        if (language.equals(Language.RUS)) {
            sendMessage.setText("Хотите Вернуться В Главное меню?");

            yesButton.setText("Да");
            yesButton.setCallbackData("YES_BUTTON");

            noButton.setText("Нет");
            noButton.setCallbackData("NO_BUTTON");
        }


        rowInLine.add(yesButton);
        rowInLine.add(noButton);


        rowsInline.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        execute(sendMessage);
    }
}