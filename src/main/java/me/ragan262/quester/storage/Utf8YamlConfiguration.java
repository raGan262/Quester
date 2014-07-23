package me.ragan262.quester.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class Utf8YamlConfiguration extends YamlConfiguration {
	
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	@Override
	public void load(final InputStream stream) throws IOException, InvalidConfigurationException {
		Validate.notNull(stream, "Stream cannot be null");
		
		final InputStreamReader reader = new InputStreamReader(stream, UTF8_CHARSET);
		final StringBuilder builder = new StringBuilder();
		final BufferedReader input = new BufferedReader(reader);
		
		try {
			String line;
			
			while((line = input.readLine()) != null) {
				builder.append(line);
				builder.append('\n');
			}
		}
		finally {
			input.close();
		}
		
		loadFromString(builder.toString());
	}
	
	@Override
	public void save(final File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		
		Files.createParentDirs(file);
		
		final String data = saveToString();
		
		final FileOutputStream stream = new FileOutputStream(file);
		final OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8_CHARSET);
		
		try {
			writer.write(data);
		}
		finally {
			writer.close();
		}
	}
}
