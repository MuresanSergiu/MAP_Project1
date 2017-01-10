package afterwind.lab1.service;

import afterwind.lab1.entity.Candidate;
import afterwind.lab1.entity.Option;
import afterwind.lab1.entity.Section;
import afterwind.lab1.repository.IRepository;
import afterwind.lab1.repository.Repository;
import afterwind.lab1.validator.OptionValidator;

/**
 * Service pentru manipularea optiunilor
 */
public class OptionService extends AbstractService<Option> {
    /**
     * Contructor pentru OptionController
     */
    public OptionService(IRepository<Option, Integer> repo) {
        super(repo);
        repo.setTableHeader(String.format("%3s | %20s | %20s", "ID", "Candidat", "Sectiune"));
    }

    public OptionService() {
        this(new Repository<>(new OptionValidator()));
    }

    /**
     * Updateaza o optiune din repository
     * @param option optiunea updatata
     * @param candidate noul candidat
     * @param section noua sectiune
     */
    public void updateOption(Option option, Candidate candidate, Section section) {
        option.setCandidate(candidate);
        option.setSection(section);
        repo.markDirty();
    }

    @Override
    public String toString() {
        return repo.toString();
    }

}