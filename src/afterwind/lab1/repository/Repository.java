package afterwind.lab1.repository;

import afterwind.lab1.entity.IIdentifiable;

import java.lang.reflect.Array;

public class Repository<T extends IIdentifiable> {

    protected T[] data;
    protected int size = 0;

    /**
     * Constructor pentru Repository
     * @param clazz tipul de date retinut in Repository
     */
    @SuppressWarnings("unchecked")
    public Repository(Class<T> clazz) {
        data = (T[]) Array.newInstance(clazz, 100);
    }

    /**
     * Getter pentru size
     * @return numarul de elemente din repository
     */
    public int getSize() {
        return size;
    }

    /**
     * Adauga o entitate in repository
     * @param e entitatea care va fi adaugata
     */
    public void add(T e) {
        if (size < data.length) {
            data[size++] = e;
        }
    }

    /**
     * Sterge o entitate din repository
     * @param e entitatea care va fi stearsa
     */
    public void remove(T e) {
        for (int i = 0; i < size; i++) {
            if (e.equals(data[i])) {
                remove(i);
                return;
            }
        }
    }
    /**
     * Sterge elementul de pe pozitia data din repository
     * @param pos pozitia entitatii in repository
     * @return entitatea stearsa
     */
    public T remove(int pos) {
        T e = data[pos];
        for (int i = pos + 1; i < size; i++) {
            data[i - 1] = data[i];
        }
        size--;
        return e;
    }

    /**
     * Getter pentru o entitate cu id-ul dat
     * @param id id-ul entitatii cautate
     * @return entitatea cu id-ul dat sau null daca aceasta nu exista
     */
    public T get(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].getId() == id) {
                return data[i];
            }
        }
        return null;
    }

    /**
     * Verifica daca o entitate cu id-ul dat exista
     * @param id identificatorul unic al entitatii
     * @return daca acesta exista in repository
     */
    public boolean contains(int id) {
        return get(id) != null;
    }

    /**
     * @return vectorul de entitati
     */
    public T[] getData() {
        return data;
    }
}
