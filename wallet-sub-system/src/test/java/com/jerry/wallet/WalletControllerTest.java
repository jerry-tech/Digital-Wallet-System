package com.jerry.wallet;



import com.jerry.wallet.dto.CreateWalletDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest extends AbstractIntegrationTest{


    private ResultActions post(String url, String json) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    /**
     * com.jerry.wallet.controller.WalletController#createWalletForUser
    */
    @Test
    void testThatCanCreateWallet() throws Exception {
        CreateWalletDto createWalletDto = new CreateWalletDto(UUID.randomUUID().toString());

        post("/wallets", gson.toJson(createWalletDto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

}
