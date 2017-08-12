package edu.ruc.visualization.hdfs.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HdfsUtil {

	private Configuration conf;
	private FileSystem fileSystem;

	static HdfsUtil _hUtil = null;

	public static HdfsUtil getHdfsUtil() {
		if (_hUtil == null) {
			try {
				_hUtil = new HdfsUtil();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _hUtil;
	}

	public HdfsUtil() throws IOException {
		this.conf = new Configuration();
		this.fileSystem = FileSystem.get(conf);
	}

	/**
	 * 上传文件，
	 * 
	 * @param localFile
	 *            本地路径
	 * @param hdfsPath
	 *            格式为hdfs://ip:port/destination
	 * @throws IOException
	 */
	public void upFile(File localFile, String hdfsPath) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(localFile));
		OutputStream out = fileSystem.create(new Path(hdfsPath));
		try {
			IOUtils.copyBytes(in, out, conf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
	}

	public void upFile(String localFile, String hdfsPath) throws IOException {
		upFile(new File(localFile), hdfsPath);
	}

	public void upFile(InputStream fileInputStream, String hdfsPath)
			throws IOException {
		InputStream in = new BufferedInputStream(fileInputStream);
		OutputStream out = fileSystem.create(new Path(hdfsPath));
		try {
			IOUtils.copyBytes(in, out, conf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close Stream
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
	}

	public void readFile(String hdfsPath) throws IOException {
		InputStream in = null;
		try {
			in = fileSystem.open(new Path(hdfsPath));
			IOUtils.copyBytes(in, System.out, 4096, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}

	public String readContent(String hdfsPath) throws IOException {
		Path path = new Path(hdfsPath);
		FSDataInputStream is = fileSystem.open(path);
		FileStatus status = fileSystem.getFileStatus(path);
		byte[] buffer = new byte[Integer.parseInt(String.valueOf(status
				.getLen()))];
		is.readFully(0, buffer);
		is.close();
		return new String(buffer);
	}

	// append file
	public void appendFile(String localFile, String hdfsPath)
			throws IOException {
		InputStream in = new FileInputStream(localFile);
		OutputStream out = fileSystem.append(new Path(hdfsPath));
		try {
			IOUtils.copyBytes(in, out, conf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
	}

	// write content
	public void appendContent(String content, String hdfsPath)
			throws IOException {
		OutputStream out = fileSystem.append(new Path(hdfsPath));
		InputStream in = new ByteArrayInputStream(content.getBytes());
		IOUtils.copyBytes(in, out, conf);
		IOUtils.closeStream(in);
		IOUtils.closeStream(out);
	}

	/**
	 * 下载文件
	 * 
	 * @param hdfsPath
	 * @param localPath
	 * @throws IOException
	 */
	public void downFile(String hdfsPath, Path localPath) throws IOException {
		FSDataInputStream in = fileSystem.open(new Path(hdfsPath));
		/* FSDataOutputStream out = fileSystem.create(localPath); */
		FileOutputStream out = new FileOutputStream(new File(
				localPath.toString()));
		try {
			// read
			IOUtils.copyBytes(in, out, conf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close Stream
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param hdfsPath
	 * @param localPath
	 * @throws IOException
	 */
	public ByteArrayInputStream downFile(String hdfsPath) throws IOException {
		InputStream in = fileSystem.open(new Path(hdfsPath));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			IOUtils.copyBytes(in, bos, conf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(in);
		}
		return new ByteArrayInputStream(bos.toByteArray());
	}

	// mkdir
	public boolean mkdir(String path) throws IOException {
		Path srcPath = new Path(path);
		boolean isOk = fileSystem.mkdirs(srcPath);
		return isOk;
	}

	// make new file
	public void createFile(String path, String content) throws IOException {
		byte[] contents = content.getBytes();
		Path srcPath = new Path(path);
		FSDataOutputStream outputStream = fileSystem.create(srcPath);
		outputStream.write(contents);
		outputStream.close();
	}

	public void createTable(String path) throws IOException {
		Path srcPath = new Path(path);
		FSDataOutputStream outputStream = fileSystem.create(srcPath);
		outputStream.close();
	}

	// delete file or directory
	public void delFile(String hdfsPath) throws IOException {
		fileSystem.delete(new Path(hdfsPath), true);
	}

	// rename file
	public void renameFile(String hdfsSourcePath, String hdfsTargetPath)
			throws IOException {
		Path frpath = new Path(hdfsSourcePath);
		Path topath = new Path(hdfsTargetPath);
		fileSystem.rename(frpath, topath);
	}

	// file isExist
	public boolean isTableExists(String metatable) throws IOException {
		Path path = new Path(metatable);
		boolean exist = fileSystem.exists(path);
		return exist;
	}

	// copy directory
	public void copyDirectory(String src, String dst) throws IOException {
		Path srcPath = new Path(dst);
		if (!fileSystem.exists(srcPath)) {
			fileSystem.mkdirs(srcPath);
		}
		// FileStatus status = fileSystem.getFileStatus(new Path(dst));
		File file = new File(src);
		// if (status.isFile()) {
		// System.exit(2);
		// } else {
		// dst = cutDir(dst);
		// }
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				_hUtil.copyDirectory(f.getPath(), dst);
			} else {
				copyFile(f.getPath(), dst + files[i].getName());
			}
		}
	}

	// copy file
	public void copyFile(String localFile, String hdfsPath) {
		try {
			fileSystem.copyFromLocalFile(new Path(localFile),
					new Path(hdfsPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
