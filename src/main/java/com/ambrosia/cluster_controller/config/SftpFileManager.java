package com.ambrosia.cluster_controller.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.springframework.stereotype.Component;

import com.ambrosia.cluster_controller.config.security.PasswordEncryptor;
import com.ambrosia.cluster_controller.model.entity.Cluster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for interactions with clusters through sftp 
 * SftpFileManager
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SftpFileManager {
    private final ClientSessionManager clientSessionManager;

    private final PasswordEncryptor passwordEncryptor;

    public boolean upload(Cluster cluster, Path path) throws IOException{
        try (var clientSession = clientSessionManager.getConnection(
            cluster.getHost(), 
            cluster.getUsername(), 
            passwordEncryptor.decode(cluster.getPassword()),
            cluster.getSshPort());
            var sftpClient = SftpClientFactory.instance().createSftpClient(clientSession)) {   
        }
        return false;
    }
    public File download(String host, String username, String password, int port, String filename, String[] paths) throws IOException{
        var clientSession = clientSessionManager.getConnection(
            host,
            username,
            passwordEncryptor.decode(password),
            port
        );
        var filepath = Paths.get(filename);
        try (
            var sftpClient = SftpClientFactory.instance().createSftpClient(clientSession);
            var fos = Files.newOutputStream(filepath);
            var zos = new ZipOutputStream(fos)) {
            for (String path : paths) {
                String dirName = Paths.get(path).getFileName().toString();
                String zipFolder = dirName + "/";
            
                zos.putNextEntry(new ZipEntry(zipFolder));
                zos.closeEntry();
                zipDir(sftpClient, path, zipFolder, zos);
            }
        }
        return filepath.toFile();
    }
    public boolean delete(Cluster cluster, String path) throws IOException{
        var clientSession = clientSessionManager.getConnection(
            cluster.getHost(), 
            cluster.getUsername(), 
            passwordEncryptor.decode(cluster.getPassword()),
            cluster.getSshPort());
        try (var sftpClient = SftpClientFactory.instance().createSftpClient(clientSession)) {
            sftpClient.remove(path);
        }
        return true;
    }

    private void zipDir(SftpClient sftpClient, String remoteDir, String entryPrefix, ZipOutputStream zos)
        throws IOException{
        var entries = sftpClient.readDir(remoteDir);

        for(SftpClient.DirEntry entry: entries){
            String filename = entry.getFilename();
            if (".".equals(filename) || "..".equals(filename) || filename.startsWith(".")) continue;
            String fullRemotePath = remoteDir+"/"+filename;
            String zipEntryName = entryPrefix + filename;
            if(entry.getAttributes().isDirectory()){
                zipDir(sftpClient, fullRemotePath, zipEntryName+"/", zos);
            } else{
                zos.putNextEntry(new ZipEntry(zipEntryName));
                try(InputStream is = sftpClient.read(fullRemotePath)){
                    is.transferTo(zos);
                }
                zos.closeEntry();
            }
        };
    }
}
