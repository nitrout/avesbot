package de.avesbot.util;

import java.util.stream.Stream;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 *
 * @author Nitrout
 */
public class LevenshteinHelper {
	
	/**
	 * Get the most  case insesnitive similar subject to search.
	 * 
	 * @param search the search for comparing with subjects
	 * @param subjects the list of subjects compared against search
	 * @return the most similar subject compared with search
	 */
	public static String geteClosestSubjectIgnoreCase(String search, String...subjects) {
		
		int minDistance = Integer.MAX_VALUE;
		int distance;
		int pos = 0;
		// use lower case strings so case differences do not increase distance
		String lSearch = search.toLowerCase();
		String[] lSubjects = Stream.of(subjects).map(String::toLowerCase).toArray(String[]::new);
		
		LevenshteinDistance levDist = new LevenshteinDistance(Math.min(search.length(), 30));
		
		for(int i = 0; i < lSubjects.length; i++) {
			distance = levDist.apply(lSearch, lSubjects[i].substring(0, Math.min(search.length(), lSubjects[i].length())));
			if(distance < minDistance) {
				minDistance = distance;
				pos = i;
			}
			if(minDistance == 0)
				break;
		}
		
		return subjects[pos];
	}
}