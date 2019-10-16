package de.immerarchiv.app;

import java.util.ArrayList;
import java.util.List;

public class Config {

	
	public ServerConfig server;
	
	public List<RepositoryConfig> repositories;
	
	public List<PathConfig> pathes;
	
	public static Config defaultConfig() {
		
		Config config = new Config();
		config.server = new ServerConfig();
		config.server.port = 8888;
		
		config.repositories = new ArrayList<RepositoryConfig>();
		
		RepositoryConfig repository = new RepositoryConfig();
		config.repositories.add(repository);
		repository.url = "https://localhost";
		repository.name = "default";
		repository.token = "-token-";

		config.pathes = new ArrayList<PathConfig>();
		PathConfig path = new PathConfig();
		config.pathes.add(path);
		path.path = "D:/Stuff";
		path.pattern = "*.pdf,*.ico";
		
		return config;
	}

}
