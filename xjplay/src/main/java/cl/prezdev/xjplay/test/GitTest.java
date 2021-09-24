package cl.prezdev.xjplay.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

/**
 * @author prez
 */
public class GitTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String REMOTE_URL = "https://github.com/pperezp/jplay";


            Collection<Ref> refs = Git.lsRemoteRepository()
                    .setRemote(REMOTE_URL)
                    .call();

            for (Ref ref : refs) {
//                List<Integer> counts = getCounts(, ref.getName());
                System.out.println("For branch: " + ref.getName());
//                System.out.println("Commits ahead : " + counts.get(0));
//                System.out.println("Commits behind : " + counts.get(1));
                System.out.println();
            }
        } catch (GitAPIException ex) {
            Logger.getLogger(GitTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static List<Integer> getCounts(Repository repository, String branchName) throws IOException {
        BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(repository, branchName);
        List<Integer> counts = new ArrayList<>();
        if (trackingStatus != null) {
            counts.add(trackingStatus.getAheadCount());
            counts.add(trackingStatus.getBehindCount());
        } else {
            System.out.println("Returned null, likely no remote tracking of branch " + branchName);
            counts.add(0);
            counts.add(0);
        }
        return counts;
    }

}
