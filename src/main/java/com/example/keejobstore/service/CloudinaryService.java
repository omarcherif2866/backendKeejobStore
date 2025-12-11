package com.example.keejobstore.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "formateurs",
                        "resource_type", "image"
                )
        );
        return uploadResult.get("secure_url").toString();
    }

    public String uploadIcon(MultipartFile file, String folder) throws IOException {
        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("folder", folder);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return uploadResult.get("url").toString();
    }

    public void deleteImage(String imageUrl) throws IOException {
        // Extraire le public_id de l'URL
        String publicId = extractPublicIdFromUrl(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        // Exemple: https://res.cloudinary.com/xxx/image/upload/v123/formateurs/image.jpg
        String[] parts = imageUrl.split("/");
        String fileNameWithExtension = parts[parts.length - 1];
        String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
        return "formateurs/" + fileName;
    }
}
