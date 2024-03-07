package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericNotFoundException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileTransferController {
    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    @ResponseBody
    @GetMapping(value="/{fileCode}", produces = MediaType.IMAGE_JPEG_VALUE)
    @Secured("ROLE_USER")
    public byte[] getJpegImage(@PathVariable("fileCode") final String fileCode) throws IOException, GenericNotFoundException {
        File file;
        try {
            file=ResourceUtils.getFile(filesUpload + "/" + fileCode);
            return FileUtils.readFileToByteArray(file);
        } catch (Exception e) {
            throw new GenericNotFoundException();
        }
    }
}
