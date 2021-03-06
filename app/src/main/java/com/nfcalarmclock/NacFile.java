package com.nfcalarmclock;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 */
public class NacFile
{
	/**
	 */
	public static class Metadata
	{

		/**
		 * Directory the file resides in.
		 */
		private String mDirectory;

		/**
		 * File name.
		 */
		private String mName;

		/**
		 * File ID.
		 */
		private long mId;

		/**
		 */
		public Metadata(String directory, String name, long id)
		{
			this.mDirectory = directory;
			this.mName = name;
			this.mId = id;
		}

		/**
		 */
		public Metadata(String name, long id)
		{
			this("", name, id);
		}

		/**
		 * @return The directory.
		 */
		public String getDirectory()
		{
			return this.mDirectory;
		}

		/**
		 * @return The file ID.
		 */
		public long getId()
		{
			return this.mId;
		}

		/**
		 * @return The file name.
		 */
		public String getName()
		{
			return this.mName;
		}

		/**
		 * @return The file path.
		 */
		public String getPath()
		{
			String dir = this.getDirectory();
			String name = this.getName();
			Locale locale = Locale.getDefault();

			return dir.isEmpty()
				? name
				: String.format(locale, "%1$s/%2$s", dir, name);
		}

		/**
		 * @return True if it is a directory, and False otherwise.
		 */
		public boolean isDirectory()
		{
			return (this.getId() == -1);
		}

		/**
		 * @return True if it is a file, and False otherwise.
		 */
		public boolean isFile()
		{
			return (this.getId() != -1);
		}

		/**
		 * Print information.
		 */
		public void print()
		{
			NacUtility.printf("Directory : %s", this.getDirectory());
			NacUtility.printf("Filename  : %s", this.getName());
			NacUtility.printf("ID        : %d", this.getId());
			NacUtility.printf("Is Dir?   : %b", this.isDirectory());
			NacUtility.printf("Is File?  : %b", this.isFile());
		}

		/**
		 * Convert the input to an external Uri.
		 */
		public Uri toExternalUri()
		{
			return ContentUris.withAppendedId(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.getId());
		}

		/**
		 * Convert the input to an internal Uri.
		 */
		public Uri toInternalUri()
		{
			return ContentUris.withAppendedId(
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI, this.getId());
		}

	}

	/**
	 * Organize files in a tree structure.
	 */
	public static class Tree
		extends NacTreeNode<String>
	{


		/**
		 * The current directory.
		 */
		private NacTreeNode<String> mDirectory;

		/**
		 */
		public Tree(String path)
		{
			super(null, path, null);
			this.setDirectory(this);
		}

		/**
		 * Add a file/folder to the given directory.
		 *
		 * @param  name  The name of the file or directory.
		 * @param  id    The content ID, used to create the content Uri.
		 */
		public void add(String name, long id)
		{
			NacTreeNode<String> dir = this.getDirectory();

			//if (dir == null)
			if ((dir == null) || (name == null) || name.isEmpty())
			{
				return;
			}

			dir.addChild(name, id);
		}

		/**
		 * @see add
		 */
		public void add(String name)
		{
			this.add(name, -1);
		}

		/**
		 * Add item to the given directory.
		 */
		public void addToDirectory(String toDirectory, String name, long id)
		{
			NacTreeNode<String> currentDir = this.getDirectory();
			NacTreeNode<String> dir = toDirectory.equals(".") ? currentDir
				: currentDir.getChild(toDirectory);

			if (dir == null)
			{
				return;
			}

			dir.addChild(name, id);
		}

		/**
		 * @see addToDirectory
		 */
		public void addToDirectory(String toDirectory, String name)
		{
			this.addToDirectory(toDirectory, name, -1);
		}

		/**
		 * Change directory.
		 */
		public void cd(String path)
		{
			NacTreeNode<String> fromDir = this.getDirectory();
			String name = NacFile.basename(path);

			if (name.equals(".."))
			{
				this.cd(fromDir.getRoot());
			}
			else
			{
				String currentDir = this.getDirectoryPath();
				String newDir = path.replace(currentDir, "");
				String[] items = this.strip(newDir).split("/");
				NacTreeNode<String> toDir = fromDir;

				for (int i=0; i < items.length; i++)
				{
					if (items[i].isEmpty())
					{
						continue;
					}

					toDir = toDir.getChild(items[i]);

					if (toDir == null)
					{
						break;
					}

					this.cd(toDir);
				}
			}
		}

		/**
		 * @see cd
		 */
		public void cd(NacTreeNode<String> dir)
		{
			if (dir == null)
			{
				return;
			}

			this.setDirectory(dir);
		}

		/**
		 * @return The current directory.
		 */
		public NacTreeNode<String> getDirectory()
		{
			return this.mDirectory;
		}

		/**
		 * @return The path of the current directory.
		 */
		public String getDirectoryPath()
		{
			NacTreeNode<String> currentDir = this.getDirectory();

			return this.getPath(currentDir);
		}

		/**
		 * @return The home directory.
		 */
		public String getHome()
		{
			return this.getKey();
		}

		/**
		 * @return The path that leads to the desired node.
		 */
		public String getPath(NacTreeNode<String> node)
		{
			if (node == null)
			{
				return "";
			}

			NacTreeNode<String> ref = node;
			String path = "";
			Locale locale = Locale.getDefault();

			while (ref != null)
			{
				String key = ref.getKey();

				if (path.isEmpty())
				{
					path = key;
				}
				else if (!key.isEmpty())
				{
					path = String.format(locale, "%1$s/%2$s", key, path);
				}

				ref = ref.getRoot();
			}

			return path;
		}

		/**
		 * List contents of the current directory.
		 */
		public List<Metadata> ls()
		{
			NacTreeNode<String> node = this.getDirectory();
			String dirPath = this.getDirectoryPath();
			List<Metadata> listing = new ArrayList<>();

			if (node == null)
			{
				return listing;
			}

			for (NacTreeNode<String> child : node.getChildren())
			{
				String name = child.getKey();
				long id = (long) child.getValue();
				Metadata metadata = new Metadata(dirPath, name, id);

				listing.add(metadata);
			}

			return listing;
		}

		/**
		 * @see ls
		 */
		public List<Metadata> ls(String path)
		{
			NacTreeNode<String> dir = this.getDirectory();
			String home = this.getHome();
			String pathKey = NacFile.basename(path);
			String dirKey = dir.getKey();

			if (dirKey.equals(pathKey) || path.equals(home))
			{
				return this.ls();
			}
			else
			{
				String[] items = this.strip(path).split("/");
				NacTreeNode<String> newDir = dir;

				for (int i=0; i < items.length; i++)
				{
					if (items[i].isEmpty())
					{
						continue;
					}

					newDir = newDir.getChild(items[i]);

					if (newDir == null)
					{
						break;
					}

					this.setDirectory(newDir);
				}


				List<Metadata> listing = this.ls();
				this.setDirectory(dir);
				return listing;
			}
		}

		/**
		 * Sorted ls.
		 */
		public List<Metadata> lsSort(String path)
		{
			List<Metadata> directories = new ArrayList<>();
			List<Metadata> files = new ArrayList<>();
			List<Metadata> list;

			for (Metadata metadata : this.ls(path))
			{
				String name = metadata.getName();
				int i = 0;

				if (metadata.isDirectory())
				{
					list = directories;
				}
				else if (metadata.isFile())
				{
					list = files;
				}
				else
				{
					continue;
				}

				for (i=0; i < list.size(); i++)
				{
					Metadata md = list.get(i);

					if (name.compareTo(md.getName()) <= 0)
					{
						break;
					}
				}

				list.add(i, metadata);
			}

			directories.addAll(files);


			return directories;
		}

		/**
		 * @see lsSort
		 */
		public List<Metadata> lsSort()
		{
			List<Metadata> directories = new ArrayList<>();
			List<Metadata> files = new ArrayList<>();
			List<Metadata> list;

			for (Metadata metadata : this.ls())
			{
				String name = metadata.getName();
				int i = 0;

				if (metadata.isDirectory())
				{
					list = directories;
				}
				else if (metadata.isFile())
				{
					list = files;
				}
				else
				{
					continue;
				}

				for (i=0; i < list.size(); i++)
				{
					Metadata md = list.get(i);

					if (name.compareTo(md.getName()) <= 0)
					{
						break;
					}
				}

				list.add(i, metadata);
			}

			directories.addAll(files);


			return directories;
		}

		/**
		 * Print the contents of the current directory.
		 */
		public void print()
		{
			for (Metadata metadata : this.ls())
			{
				NacUtility.printf("NacFile.Tree : print : %s", metadata.getPath());
			}
		}

		/**
		 * Strip the home directory away from a path.
		 */
		public String strip(String path)
		{
			String home = this.getHome();
			String reducedPath = path.replace(home, "");
			String strippedPath = NacFile.strip(reducedPath);

			if (strippedPath.isEmpty())
			{
				return "";
			}

			if (strippedPath.charAt(0) == '/')
			{
				strippedPath = strippedPath.substring(1);
			}

			return strippedPath;
		}

		/**
		 * Set the current directory.
		 */
		public void setDirectory(NacTreeNode<String> dir)
		{
			this.mDirectory = dir;
		}

		/**
		 * Convert relative path to absolute path, given a file name.
		 */
		public String relativeToAbsolutePath(String relativePath, String name)
		{
			for (Metadata metadata : this.lsSort(relativePath))
			{
				if (metadata.getName().equals(name))
				{
					return metadata.getPath();
				}
			}

			return relativePath;
		}

	}

	/**
	 * @return The basename of a file path.
	 */
	public static String basename(String path)
	{
		if ((path == null) || path.isEmpty())
		{
			return "";
		}

		//String[] items = this.strip(path).split("/");
		String[] items = path.split("/");

		return (items.length > 0) ? items[items.length-1] : "";
	}

	/**
	 * @return The dirname of a file path.
	 */
	public static String dirname(String path)
	{
		String basename = NacFile.basename(path);

		return path.substring(0, path.length()-basename.length());
	}

	/**
	 * Remove extension from file name.
	 */
	public static String removeExtension(String name)
	{
		return (name.contains(".")) ?
			name.substring(0, name.lastIndexOf('.')) : name;
	}

	/**
	 * Strip away any trailing '/' characters.
	 */
	public static String strip(String path)
	{
		if (path == null)
		{
			return "";
		}

		String normalPath = path;
		int length = path.length();

		if (length == 0)
		{
			return "";
		}

		if (normalPath.charAt(length-1) == '/')
		{
			normalPath= normalPath.substring(0, length-1);
		}

		return normalPath;
	}

	/**
	 * Convert a path to a relative path.
	 */
	@SuppressWarnings("SdCardPath")
	public static String toRelativePath(String path)
	{
		String emulated = "/storage/emulated/0";
		String sdcard = "/sdcard";
		String relativePath = path;

		if (path.startsWith(emulated))
		{
			relativePath = path.substring(emulated.length());
		}
		else if (path.startsWith(sdcard))
		{
			relativePath = path.substring(sdcard.length());
		}

		if (!relativePath.isEmpty() && (relativePath.charAt(0) == '/'))
		{
			relativePath = relativePath.substring(1);
		}

		return relativePath;
	}

}
