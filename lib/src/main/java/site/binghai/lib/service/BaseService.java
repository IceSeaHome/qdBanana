package site.binghai.lib.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.utils.BaseBean;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseService<T extends BaseEntity> extends BaseBean {
    @Autowired
    private EntityManager entityManager;
    private SimpleJpaRepository<T, Long> daoHolder;

    public T newInstance(Map map) {
        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        return obj.toJavaObject(getTypeArguement());
    }

    @Transactional
    public T newAndSave(Map map) {
        return save(newInstance(map));
    }

    protected JpaRepository<T, Long> getDao() {
        if (daoHolder != null) {
            return daoHolder;
        }
        daoHolder = new SimpleJpaRepository(getTypeArguement(), entityManager);
        return daoHolder;
    }

    /**
     * 获取T的实际类型
     */
    public Class<T> getTypeArguement() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    @Transactional
    public T save(T t) {
        return getDao().save(t);
    }

    @Transactional
    public T update(T t) {
        if (t.getId() >= 0) {
            return save(t);
        }
        return t;
    }

    public T findById(Long id) {
        if (id == null) return null;
        return getDao().findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        getDao().deleteById(id);
    }

    @Transactional
    public T deleteIfExist(Long id) {
        T t = findById(id);
        if (t == null) return null;
        delete(id);
        return t;
    }

    @Transactional
    public boolean deleteAll(String confirm) {
        if (confirm.equals("confirm")) {
            getDao().deleteAll();
            return true;
        }
        return false;
    }

    public List<T> findByIds(List<Long> ids) {
        List<T> rs = getDao().findAllById(ids);
        return rs == null ? null : new ArrayList<>(rs);
    }

    public List<T> query(T example) {
        example.setCreated(null);
        example.setCreatedTime(null);
        Example<T> ex = Example.of(example);
        List<T> rs = getDao().findAll(ex);

        return rs == null ? null : new ArrayList<>(rs);
    }

    public T queryOne(T example) {
        example.setCreated(null);
        example.setCreatedTime(null);
        Example<T> ex = Example.of(example);
        Optional<T> rs = getDao().findOne(ex);
        return rs == null ? null : rs.orElse(null);
    }

    public List<T> sortQuery(T example, String sortField, Boolean desc) {
        example.setCreated(null);
        example.setCreatedTime(null);
        Example<T> ex = Example.of(example);
        List<T> rs = getDao().findAll(ex, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortField));
        return rs == null ? null : new ArrayList<>(rs);

    }

    public List<T> findAll(int limit) {
        List<T> rs = getDao().findAll(new PageRequest(0, limit)).getContent();
        return rs == null ? null : new ArrayList<>(rs);
    }

    public List<T> findAll(int page, int pageSize) {
        List<T> rs = getDao().findAll(new PageRequest(page, pageSize)).getContent();
        return rs == null ? null : new ArrayList<>(rs);
    }

    public long count() {
        return getDao().count();
    }

    @Transactional
    public void batchSave(List<T> batch) {
        getDao().saveAll(batch);
    }

    /**
     * 使用map更新entity中除id外的其他字段
     */
    public T updateParams(T t, Map map) {
        Long id = t.getId();
        JSONObject item = JSONObject.parseObject(JSONObject.toJSONString(t));
        item.putAll(map);
        item.put("id", id);
        return item.toJavaObject(getTypeArguement());
    }

    @Transactional
    public T updateAndSave(BaseEntity admin, Map map) throws Exception {
        Long id = getLong(map, "id");
        if (id == null) {
            throw new Exception("id must be present!");
        }
        T old = findById(id);
        if (old == null) {
            throw new Exception("item not exist!");
        }
        T new_ = updateParams(old, map);
        logger.warn("{} update {} from {} to {}", admin, getTypeArguement().getSimpleName(), old, new_);
        return update(new_);
    }
}


