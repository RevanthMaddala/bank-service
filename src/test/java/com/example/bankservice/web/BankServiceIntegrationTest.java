package com.example.bankservice.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String uniqueIfsc() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TEST0" + suffix;
    }

    private String bankJson(String ifsc, String branch) {
        return """
                {
                  "bankName": "Test Bank",
                  "ifscCode": "%s",
                  "branchName": "%s",
                  "address": {
                    "street": "12 Residency Road",
                    "city": "Bengaluru",
                    "state": "Karnataka",
                    "pinCode": "560025"
                  }
                }
                """.formatted(ifsc, branch);
    }

    private long createBank(String ifsc) throws Exception {
        String body = mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bankJson(ifsc, "Residency Road")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private String createAccount(long bankId, String openingBalance) throws Exception {
        String body = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountHolderName":"Asha Rao","openingBalance":%s,
                                 "accountType":"SAVINGS","bankId":%d}
                                """.formatted(openingBalance, bankId)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.get("accountNumber").asText();
    }

    @Test
    void createAndFetchBankWithAddress() throws Exception {
        String ifsc = uniqueIfsc();
        long id = createBank(ifsc);

        mockMvc.perform(get("/api/v1/banks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ifscCode").value(ifsc))
                .andExpect(jsonPath("$.address.city").value("Bengaluru"))
                .andExpect(jsonPath("$.address.pinCode").value("560025"));

        mockMvc.perform(get("/api/v1/banks/ifsc/{ifsc}", ifsc.toLowerCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void duplicateIfscIsRejected() throws Exception {
        String ifsc = uniqueIfsc();
        createBank(ifsc);

        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bankJson(ifsc, "Another Branch")))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidBankRequestReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bankName":"","ifscCode":"BAD","branchName":"X",
                                 "address":{"street":"s","city":"c","state":"st","pinCode":"012345"}}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("bankName")))
                .andExpect(jsonPath("$.errors", hasKey("ifscCode")))
                .andExpect(jsonPath("$.errors", hasKey("address.pinCode")));
    }

    @Test
    void updateBankAndAddress() throws Exception {
        long id = createBank(uniqueIfsc());
        String newIfsc = uniqueIfsc();

        mockMvc.perform(put("/api/v1/banks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bankJson(newIfsc, "Koramangala")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ifscCode").value(newIfsc))
                .andExpect(jsonPath("$.branchName").value("Koramangala"));
    }

    @Test
    void accountLifecycle() throws Exception {
        long bankId = createBank(uniqueIfsc());
        String accountNumber = createAccount(bankId, "1000.00");

        mockMvc.perform(get("/api/v1/accounts/{n}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.bank.id").value(bankId))
                .andExpect(jsonPath("$.bank.address.state").value("Karnataka"));

        mockMvc.perform(post("/api/v1/accounts/{n}/deposit", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 250.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1250.50));

        mockMvc.perform(post("/api/v1/accounts/{n}/withdraw", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 5000}"))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/api/v1/accounts/{n}/withdraw", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 250.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00));

        mockMvc.perform(put("/api/v1/accounts/{n}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountHolderName\":\"Asha R\",\"accountType\":\"CURRENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("CURRENT"));

        mockMvc.perform(get("/api/v1/accounts").param("bankId", String.valueOf(bankId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        // Bank with accounts cannot be deleted
        mockMvc.perform(delete("/api/v1/banks/{id}", bankId))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/v1/accounts/{n}", accountNumber))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/accounts/{n}", accountNumber))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/v1/banks/{id}", bankId))
                .andExpect(status().isNoContent());
    }

    @Test
    void invalidAmountsAreRejected() throws Exception {
        String accountNumber = createAccount(createBank(uniqueIfsc()), "10");

        mockMvc.perform(post("/api/v1/accounts/{n}/deposit", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/accounts/{n}/deposit", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 1.234}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void accountForUnknownBankReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountHolderName":"X","openingBalance":0,
                                 "accountType":"SAVINGS","bankId":999999}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidSortPropertyReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/banks").param("sort", "doesNotExist"))
                .andExpect(status().isBadRequest());
    }
}
