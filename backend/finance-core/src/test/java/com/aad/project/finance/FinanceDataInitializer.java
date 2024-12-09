package com.aad.project.finance;

import com.aad.project.finance.access.CategoryAccess;
import com.aad.project.finance.access.TransactionAccess;
import com.aad.project.finance.access.UserAccess;
import com.aad.project.finance.access.UserProfileAccess;
import com.aad.project.finance.tables.Category;
import com.aad.project.finance.tables.Transaction;
import com.aad.project.finance.tables.User;
import com.aad.project.finance.tables.UserProfile;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class FinanceDataInitializer implements CommandLineRunner {

    @Autowired
    private UserAccess userAccess;

    @Autowired
    private UserProfileAccess userProfileAccess;

    @Autowired
    private CategoryAccess categoryAccess;

    @Autowired
    private TransactionAccess transactionAccess;

    public static void main(String[] args) {
        SpringApplication.run(FinanceDataInitializer.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        categoryAccess.deleteAll();
        transactionAccess.deleteAll();
        userProfileAccess.deleteAll();
        userAccess.deleteAll();

        generateTestData();
        generateTestData(userAccess.findAll(), categoryAccess.findAll());
    }

    private void generateTestData() {
        Faker faker = new Faker();

        // Generate Categories
        for (int i = 0; i < 5; i++) {
            Category category = new Category();
            category.setName(faker.commerce().department());
            category.setDescription(faker.lorem().sentence());
            categoryAccess.save(category);
        }
        User user1 = new User();
        user1.setName("wow");
        user1.setEmail("wow@wow.com");
        user1.setPassword("wow");
        userAccess.save(user1);
        UserProfile userProfile1 = new UserProfile();
        userProfile1.setUser(user1);
        userProfile1.setMonthlyIncome(faker.number().randomDouble(2, 2000, 10000));
        userProfile1.setSavingsGoal(faker.number().randomDouble(2, 1000, 5000));
        userProfileAccess.save(userProfile1);
        // Generate Users
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword("password");
            userAccess.save(user);

            // Generate UserProfile
            UserProfile userProfile = new UserProfile();
            userProfile.setUser(user);
            userProfile.setMonthlyIncome(faker.number().randomDouble(2, 2000, 10000));
            userProfile.setSavingsGoal(faker.number().randomDouble(2, 1000, 5000));
            userProfileAccess.save(userProfile);
        }
    }

    private void generateTestData(List<User> users, List<Category> categories) {
        Faker faker = new Faker();

        // Generate Transactions for Users
        for (User user : users) {
            for (int i = 0; i < faker.random().nextInt(1, 10); i++) {
                Transaction transaction = new Transaction();
                Category category = getRandomCategory(categories);

                transaction.setUser(user);
                transaction.setType(faker.random().nextBoolean() ? "income" : "expense");
                transaction.setCategory(category);
                transaction.setAmount(faker.number().randomDouble(2, 50, 1000));
                transaction.setDate(generateRandomDate());
                transactionAccess.save(transaction);
            }
        }
    }

    public User getRandomUser(List<User> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(users.size());
        return users.get(randomIndex);
    }

    public Category getRandomCategory(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(categories.size());
        return categories.get(randomIndex);
    }

    private Long generateRandomDate() {
        Random random = new Random();
        int year = 2023;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // To avoid invalid dates
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0); // Midnight of the generated date
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(); // Convert to epoch seconds
    }

}
