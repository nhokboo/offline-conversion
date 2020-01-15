package nhanhvn.security.apistorage;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ApiHelper {
	public static ApiCredentials getApiCredentials() {
		Yaml yamlLoader = new Yaml(new Constructor(ApiCredentials.class));
		InputStream input = null;
		try {
			input = new FileInputStream(new File("resources/Configurations/configurations.yaml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return yamlLoader.load(input);
	}
}
