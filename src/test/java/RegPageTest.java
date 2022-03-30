import com.UserOperations;
import com.page.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static com.page.MainPage.MAIN_PAGE_URL;
import static org.junit.Assert.*;

public class RegPageTest extends Config {
    Map<String, String> newUser = new UserOperations().register();
    String email = newUser.get("email");
    String password = newUser.get("password");
    String name = newUser.get("name");
    MainPage mainPage;
    RegPage regPage = page(RegPage.class);

    @Before
    public void before() {
        mainPage = open(MAIN_PAGE_URL, MainPage.class);
    }

    @After
    public void tearDown() {
        UserOperations.delete();
        webdriver().driver().close();
    }

    @Test
    @DisplayName("Проверка, что пользователь может зарегистрироваться и затем авторизоваться")
    @Description("При успешном входе на Главной появится кнопка «Оформить заказ»")
    public void successRegistrationNewUserTest() {
        mainPage
                .signInButtonClick()
                .registerLinkClick()
                .setName(name)
                .setEmail("11"+email) //сгенерированные email стали повторяться, добавлены символы для исключения повторов
                .setPassword(password)
                .regButtonClick()
                .setEmail("11"+email)
                .setPassword(password)
                .enterButtonClick();
        boolean isExecuteOrderButtonDisplayedNow = page(MainPage.class).executeOrderButtonVisible();
        assertTrue("Кнопка «Оформить заказ» не появилась", isExecuteOrderButtonDisplayedNow);
    }

    @Test
    @DisplayName("Проверка, что пользователь не может зарегистрироваться с уже ранее зарегистрированным email")
    @Description("Появится ошибка «Такой пользователь уже существует»")
    public void duplicatedRegistrationNewUserTest() {
        mainPage
                .signInButtonClick()
                .registerLinkClick()
                .setName(name)
                .setEmail("11"+email)
                .setPassword(password)
                .regButtonClick()
                .registerLinkClick()
                .setName(name)
                .setEmail("11"+email)
                .setPassword(password)
                .regButtonClick();
        assertTrue("Ошибка при создании пользователя с таким же email не отображается", regPage.isEmailCorrect());
    }


    @Test
    @DisplayName("Проверка, что пользователь не может зарегистрироваться с паролем 5 символов")
    @Description("Появится ошибка «Некорректный пароль»")
    public void registrationNewUserWithIncorrectPasswordTest() {
        String wrongPass = "12345";
        mainPage
                .signInButtonClick()
                .registerLinkClick()
                .setName(name)
                .setEmail(email)
                .setPassword(wrongPass)
                .regButtonClick();
        assertTrue("Ошибка «Некорректный пароль» не появилась", regPage.isWrongPassErrorMessageVisible());
    }

    @Test
    @DisplayName("Проверка, что пользователь не может зарегистрироваться с пустым паролем")
    @Description("Появится ошибка «Некорректный пароль»")
    public void registrationNewUserWithEmptyPasswordTest() {
        String wrongPass = "";
        mainPage
                .signInButtonClick()
                .registerLinkClick()
                .setName(name)
                .setEmail(email)
                .setPassword(wrongPass)
                .regButtonClick();
        assertTrue("Ошибка «Некорректный пароль» не появилась", regPage.isWrongPassErrorMessageVisible());
    }
}
