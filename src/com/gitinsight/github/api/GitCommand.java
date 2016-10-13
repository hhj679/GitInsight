package com.gitinsight.github.api;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class GitCommand {

	private static final String REMOTE_URL = "https://github.com/github/testrepo.git";
	
	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		// TODO Auto-generated method stub
		File localPath = File.createTempFile("F:\\gitinsight\\github\\data\\projects\\TestGitRepository", "");
        localPath.delete();

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call();

        try {
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
	        System.out.println("Having repository: " + result.getRepository().getDirectory());
        } finally {
        	result.getRepository().close();
        	result.close();
        }
	}

}
