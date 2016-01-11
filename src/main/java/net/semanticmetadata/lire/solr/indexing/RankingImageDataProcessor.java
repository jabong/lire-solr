package net.semanticmetadata.lire.solr.indexing;

import net.semanticmetadata.lire.indexing.parallel.WorkItem;

public class RankingImageDataProcessor implements ImageDataProcessor {

	@Override
	public CharSequence getTitle(String filename) {
		return null;
	}

	@Override
	public CharSequence getIdentifier(String filename) {
		return null;
	}

	@Override
	public CharSequence getAdditionalFields(String filename) {
		return null;
	}
	
}
