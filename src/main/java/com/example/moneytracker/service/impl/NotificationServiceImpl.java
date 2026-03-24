package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.repository.ProfileRepository;
import com.example.moneytracker.service.EmailService;
import com.example.moneytracker.service.ExpenseService;
import com.example.moneytracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;
     @Value("${money.manager.frontend.url}")
    private String frontendurl;

    @Override
    @Scheduled(cron = "0 0 22 * * *", zone ="IST")
    public void sendDailyIncomeExpensesReminder() {

        log.info("Job started: Sending Daily Income Expenses Reminder");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {

            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today.<br><br>"
                    + "<a href='" + frontendurl + "' "
                    + "style='background-color:#4CAF50;color:#fff;text-decoration:none;"
                    + "border-radius:5px;padding:10px;font-weight:bold;'>"
                    + "Go to Money Manager</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";

            emailService.sendEmail(
                    profile.getEmail(),
                    "Daily Reminder",
                    body
            );
        }
        log.info("Job finished: Sending  Expenses Reminder successfully");
    }

    @Override
    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpensesSummary() {

        log.info("Job started: Sending Daily Expenses Summary");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {

            List<ExpenseDto> todaysExpenses =
                    expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if (todaysExpenses.isEmpty()) {
                continue;
            }

            StringBuilder table = new StringBuilder();

            // Table start
            table.append("<table style='border-collapse:collapse;width:100%;'>");

            // Header row
            table.append("<tr style='background-color:#f2f2f2;'>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                    .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                    .append("</tr>");

            int i = 1;

            for (ExpenseDto expense : todaysExpenses) {

                table.append("<tr>");

                table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                        .append(i++)
                        .append("</td>");

                table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                        .append(expense.getName())
                        .append("</td>");

                table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                        .append(expense.getAmount())
                        .append("</td>");

                table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                        .append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                        .append("</td>");

                table.append("</tr>");
            }

            table.append("</table>");

            // Email body
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "Here is a summary of your expenses for today:<br><br>"
                    + table
                    + "<br><br>Best regards,<br>Money Manager Team";

            emailService.sendEmail(
                    profile.getEmail(),
                    "Your Daily Expense Summary",
                    body
            );
        }

    }




    }

