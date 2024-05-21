package com.sbl.demo.sblproject.util;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.NullHelper;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileComponent extends Base {


    final String rootDir = "C:\\upload\\";
    final int MAX_FILE_NAME_LEN = 100;
    final String[] ALLOW_EXT_FILE_ZIP = {".zip", ".hgx", ".tgz"};
    final String[] ALLOW_EXT_FILE_IMG = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
    final String[] ALLOW_EXT_FILE_DOC = {".pdf", ".hwp", ".xls", ".xlsx", ".doc",
        ".docx", ".ppt", ".pptx"};
    final String[] ALLOW_EXT_FILE_AUDIO = {".mp3", ".ogg", ".wma", ".wav", ".au",
        ".rm", ".mid"};
    final String[] ALLOW_EXT_FILE_VIDEO = {".mkv", ".avi", ".mp4", ".mpg", ".flv",
        ".wmv", ".asf", ".asx", ".ogm", ".ogv", ".mov"};


    public void uploadFile(MultipartFile file, String filePath, String[] extList, long maxFileSize)
        throws Exception {

        try {
            if ((file == null) || (file.isEmpty())) {
                throw new FileAlreadyExistsException("파일을 확인해주세요.");
            }
            if (filePath == null) {
                throw new FileUploadException("파일 저장경로를 확인해주세요.");
            }
            final String regex = "^.*[\\\\/]";
            final String originalFilename = file.getOriginalFilename()
                .replaceAll(regex, ""); // 원본 파일명
            final String originalFileExtension = getFileExtension(originalFilename); // 원본 파일 확장자
            final long fileSize = file.getSize();

            if (originalFilename.length() > MAX_FILE_NAME_LEN) {
                throw new FileUploadException(
                    String.format("파일명은 %s자를 넘을 수 없습니다.", MAX_FILE_NAME_LEN));
            }
            if ((NullHelper.isNull(extList)) || !(extList.length > 0)) {
                extList = getAllowExts();
            }
            if (!Arrays.asList(extList).contains(originalFileExtension)) {
                throw new FileUploadException(
                    String.format("지원하지 않는 파일 확장자입니다.(%s)", originalFileExtension));
            }
            if ((fileSize > 0) && (fileSize > maxFileSize)) {
                throw new FileUploadException(
                    String.format("저장 가능한 파일크기(%s)를 초과하였습니다.", maxFileSize));
            }

            String storedPath = String.format("%s%s%s", rootDir, File.separator, filePath);
            File dir = new File(storedPath);
            if (!dir.exists()) {
                FileUtils.forceMkdir(dir);
            }

            File newFile = getUniqueFile(storedPath, originalFilename, originalFileExtension);
            file.transferTo(newFile); // 파일 업로드

        } catch (Exception e) {
            throw new Exception();
        }

    }

    public void deleteFile(String filePath, String fileName) throws FileUploadException {
        try {
            String storedPath = String.format("%s%s%s%s", rootDir, filePath, File.separator,
                fileName);
            File file = new File(storedPath);
            FileUtils.delete(file);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new FileUploadException("파일을 삭제 하는데 오류가 발생했습니다.");
        }
    }



    private String getFileExtension(String filename) {
        String fileExtension = "";
        if (!NullHelper.isEmpty(filename)) {
            if (filename.lastIndexOf(".") != -1) {
                fileExtension = filename.toLowerCase()
                    .substring(filename.lastIndexOf("."), filename.length());
            }
        }
        return fileExtension;
    }

    private String[] getAllowExts() {
        return Stream.of(ALLOW_EXT_FILE_ZIP
                , ALLOW_EXT_FILE_IMG
                , ALLOW_EXT_FILE_DOC
                , ALLOW_EXT_FILE_AUDIO
                , ALLOW_EXT_FILE_VIDEO)
            .flatMap(Arrays::stream)
            .toArray(String[]::new);
    }

    private File getUniqueFile(String storedPath, String originalFilename,
        String originalFileExtension) {
        File file = new File(storedPath, originalFilename);
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String extension = originalFileExtension;
        int count = 1;

        while (file.exists()) {
            file = new File(storedPath, baseName + "(" + count + ")" + extension);
            count++;
        }

        return file;
    }
}
