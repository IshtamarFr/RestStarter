package fr.ishtamar.starter.unit;

import fr.ishtamar.starter.filetransfer.FileUploadUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileUploadUtilTest {
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    File folder;

    int countFiles() {
        File[] files = folder.listFiles();
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                count++;
            }
        }
        return count;
    }

    @AfterEach
    @BeforeEach
    void clean(){
        try{
            folder= ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("When I upload a file, it is stored in folder")
    void testUploadFile() throws Exception{
        //Given

        //When
        fileUploadUtil.saveFile("dixee.jpg",new MockMultipartFile("dixee",new byte[0]));

        //Then
        assertThat(countFiles()).isEqualTo(1);
    }
}
