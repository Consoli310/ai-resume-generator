package consoli.resume.repository;

import consoli.resume.entity.ResumeEntity;
import consoli.resume.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository
        extends JpaRepository<
        ResumeEntity,
        Long
        > {

    Page<ResumeEntity>
    findByUser(

            UserEntity user,
            Pageable pageable

    );
}