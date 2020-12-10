package main.service;

import main.api.response.result.ImageResultResponse;
import main.exception.InvalidParameterException;
import main.model.enums.FileExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Random;

/**
 * Класс работы с изображениями
 */
@Service
public class ImageService {
    @Autowired
    private HttpServletRequest request;

    @Value("${file.max-file-size}")
    private int maxFileSize;
    @Value("${file.max-image-height}")
    private int imageTargetHeight;
    @Value("${file.max-image-width}")
    private int imageTargetWidth;

    @Value("${file.upload-directory}")
    private String uploadPath;

    /**
     * Метод загрузки изображения
     * @param image загружаемое изображение
     * @return ImageResultResponse с путем до загружаемого изображения
     * @throws IOException в случае ошибки чтения
     * @throws InvalidParameterException в случае ошибок обработки изображения (превышен размер, неверный формат и т.д.)
     */
    public ImageResultResponse loadImage(MultipartFile image) throws IOException, InvalidParameterException {
        checkFileSize(image, maxFileSize);
        checkFileExtension(image);

        // создаем папку (при отсутствии) и сохраняем файл
        if (Files.notExists(Paths.get(uploadPath))) {
            new File(uploadPath).mkdir();
        }
        Files.copy(image.getInputStream(), Paths.get(uploadPath, image.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

        return new ImageResultResponse(true, "/" + uploadPath + "/" + image.getOriginalFilename());
    }

    /**
     * Метод проверки размера файла
     * @param file файл для проверки размера
     * @param sizeInMB максимально допустимый размер файла
     * @throws InvalidParameterException в случае, если размер файла больше допустимого размера
     */
    public void checkFileSize(MultipartFile file, int sizeInMB) throws InvalidParameterException {
        if (file.isEmpty()) {
            throw new InvalidParameterException("image", "Ошибка при загрузке изображения");
        }

        double actualSize = file.getSize() * 0.00000095367432;

        if (actualSize > sizeInMB) {
            throw new InvalidParameterException("image", String.format("Размер файла превышает допустимый размер (%sMB)", maxFileSize));
        }
    }

    /**
     * Метод проверяет доступные расширения для загружаемого файла
     * @param file загружаемый файл
     * @throws InvalidParameterException в случаях, когда не удается прочитать расширение файла
     */
    private void checkFileExtension(MultipartFile file) throws InvalidParameterException {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new InvalidParameterException("image", "Изображение не имеет расширения");
        }

        String[] fileFrags = file.getOriginalFilename().split("\\.");
        String extension = fileFrags[fileFrags.length-1];

        for (FileExtension fileExtension : FileExtension.values()) {
            if (fileExtension.name().equalsIgnoreCase(extension)) {
                return;
            }
        }

        throw new InvalidParameterException("image", "Недопустимый тип файла");
    }

    /**
     * Метод генерирует случайную строку из определенного кол-ва символов
     * @param length длина строки
     * @return String
     */
    public static String generateFolder(int length) {
        Random r = new Random();
        String s = r.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return s;
    }

    /**
     * Метод изменяет размер и сохраняет изображение
     * @param image изменячемое изображение
     * @return путь до измененного изображения
     * @throws IOException в случае ошибок чтения изображения
     */
    public String resizeAndWriteImage(MultipartFile image) throws IOException, InvalidParameterException {
        checkFileSize(image, maxFileSize);
        checkFileExtension(image);

        Path scaledPath = Paths.get(uploadPath).resolve("scaled");

        // создаем папку при ее отсутствии
        if (Files.notExists(scaledPath)) {
            new File(scaledPath.toString()).mkdirs();
        }

        String destPath = scaledPath.toString() + "/" + image.getOriginalFilename();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
        BufferedImage scaledImage = getScaledInstance(bufferedImage, imageTargetWidth, imageTargetHeight,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);

        writePNG(scaledImage, new FileOutputStream(destPath), 0.85f);
        return destPath;
    }

    /**
     * Метод изменяет изображение на заданную ширину и высоту
     */
    private BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight,
                                            Object hint, boolean higherQuality) {
        int type =
                (img.getTransparency() == Transparency.OPAQUE)
                        ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

    /**
     * Метод записывает измененный файл в указанное место
     */
    private void writePNG(BufferedImage bufferedImage, OutputStream outputStream,
                                float quality) throws IOException {
        Iterator<ImageWriter> iterator =
                ImageIO.getImageWritersByFormatName(FileExtension.PNG.name().toLowerCase());
        ImageWriter imageWriter = iterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);

        ImageOutputStream imageOutputStream =
                new MemoryCacheImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);
        IIOImage iioimage = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, iioimage, imageWriteParam);
        imageOutputStream.flush();
        imageOutputStream.close();
        outputStream.close();
    }
}