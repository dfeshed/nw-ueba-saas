package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdUserThumbnailServiceImpl implements AdUserThumbnailService {

    private static final Logger logger = Logger.getLogger(AdUserThumbnailServiceImpl.class);

    @Autowired
    private AdUserThumbnailRepository adUserThumbnailRepository;

    @Override
    public AdUserThumbnail findById(String objectGUID) {
        return adUserThumbnailRepository.findById(objectGUID);
    }

    @Override
    public List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails) {
        return adUserThumbnailRepository.save(adUserThumbnails);
    }




}
