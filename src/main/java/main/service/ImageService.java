package main.service;

import main.Main;
import main.api.response.result.ImageResultResponse;
import main.exception.InvalidParameterException;
import main.model.enums.FileExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
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
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");
    private static final String SEPARATOR = File.separator;

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
     * @return ImageResultResponse с путем до загружаемого изображения (/upload/image.jpg)
     * @throws IOException в случае ошибки чтения/записи
     * @throws InvalidParameterException в случае ошибок обработки изображения (превышен размер, неверный формат и т.д.)
     */
    public ImageResultResponse loadImage(MultipartFile image) throws IOException {
        checkFileSize(image, maxFileSize);
        checkFileExtension(image);

        String randomUploadPath = uploadPath + generateFolder(3, 2);

        // создаем папку (при отсутствии) и сохраняем файл
        if (Files.notExists(Paths.get(randomUploadPath))) {
            new File(randomUploadPath).mkdirs();
        }
        Files.copy(image.getInputStream(), Paths.get(randomUploadPath, image.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

        return new ImageResultResponse(true, "\\" + replaceFrontToBackSlash(randomUploadPath) + "\\" + image.getOriginalFilename());
    }

    /**
     * Метод проверки размера файла
     * @param file файл для проверки размера
     * @param sizeInMB максимально допустимый размер файла
     * @throws InvalidParameterException в случае, если размер файла больше допустимого размера
     */
    public void checkFileSize(MultipartFile file, int sizeInMB) {
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
    private void checkFileExtension(MultipartFile file) {
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
     * @param folderCount кол-во вложенныхпапок
     * @param folderLength кол-во символов в названии подпапки
     * @return String - сгенерированное название папки
     */
    public static String generateFolder(int folderCount, int folderLength) {
        StringBuilder path = new StringBuilder();
        Random r = new Random();

        for (int i = 0; i < folderCount; i++) {
            String s = r.ints(48, 122)
                    .filter(el -> (el < 57 || el > 65) && (el < 90 || el > 97))
                    .mapToObj(el -> (char) el)
                    .limit(folderLength)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
            path.append(SEPARATOR).append(s);
        }

        return path.toString();
    }

    /**
     * Метод изменяет размер и сохраняет изображение
     * @param image изменячемое изображение
     * @return путь до измененного изображения
     * @throws IOException в случае ошибок чтения/записи изображения
     */
    public String resizeAndWriteImage(int userId, MultipartFile image) throws IOException {
        checkFileSize(image, maxFileSize);
        checkFileExtension(image);

        Path scaledPath = Paths.get(uploadPath + SEPARATOR + "scaled" + SEPARATOR + userId);
        String destPath = scaledPath.toString() + SEPARATOR + image.getOriginalFilename();

        // создаем папку при ее отсутствии
        if (Files.notExists(scaledPath)) {
            new File(scaledPath.toString()).mkdirs();
        }

        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
        BufferedImage scaledImage = getScaledInstance(bufferedImage, imageTargetWidth, imageTargetHeight,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
        writePNG(scaledImage, new FileOutputStream(destPath), 0.85f);
        return "\\" + replaceFrontToBackSlash(destPath);
    }

    /**
     * Метод изменения прямого слеша на обратный
     * @param string строка с прямыми слешами
     * @return String, строка с обратными слешами
     */
    private String replaceFrontToBackSlash(String string) {
        return string.replace("/", "\\");
    }

    /**
     * Метод изменяет изображение на заданную ширину и высоту
     */
    private BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight,
                                            Object hint, boolean higherQuality) {
        int type =
                (img.getTransparency() == Transparency.OPAQUE)
                        ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
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
    private void writePNG(BufferedImage bufferedImage, OutputStream outputStream, float quality) throws IOException {
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

    /**
     * Метод удаления файла по указанному пути
     * @param destinationPath путь к файлу
     */
    public void removePhoto(String destinationPath) {
        if (destinationPath == null || destinationPath.isEmpty()) {
            return;
        }

        File file = new File(destinationPath);
        if (!file.delete()) {
            LOGGER.info(MARKER, "ошибка при удалении файла: {}", destinationPath);
        }
    }

    /**
     * Метод преобразования изображения в массив байтов
     */
    public byte[] imageToBytes(BufferedImage image, String imageFormat) {
        byte[] bytes = new byte[0];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageFormat, baos);
            baos.flush();
            bytes = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * Метод изменения размера изображения
     */
    public BufferedImage resizeImage(BufferedImage image, int newHeight, int newWidth) {
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Image resultingImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }
}