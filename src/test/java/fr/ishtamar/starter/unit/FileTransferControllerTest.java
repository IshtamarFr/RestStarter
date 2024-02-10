package fr.ishtamar.starter.unit;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class FileTransferControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    File folder;

    @BeforeEach
    void init(){
        try{
            folder=ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
            FileUtils.copyFileToDirectory(ResourceUtils.getFile("src/test/resources/dixee.jpg"),folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @AfterEach
    void clean(){
        try{
            folder=ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("When I try to get a picture saved, I can see it")
    @WithMockUser(roles="USER")
    void testGetPicture() throws Exception {
        //Given

        //When
        this.mockMvc.perform(get("/file/dixee.jpg"))

        //Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When I try to get an inexistant picture, I get an error")
    @WithMockUser(roles="USER")
    void testGetInexistantPicture() throws Exception {
        //Given

        //When
        this.mockMvc.perform(get("/file/dixee2.jpg"))

                //Then
                .andExpect(status().isNotFound());
    }
}
