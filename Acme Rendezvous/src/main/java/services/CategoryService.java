
package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CategoryRepository;
import domain.Admin;
import domain.Category;

@Service
@Transactional
public class CategoryService {

	// Managed Repository
	@Autowired
	private CategoryRepository		categoryRepository;

	// Supporting services

	@Autowired
	private AdminService	adminService;


	// Constructors

	public CategoryService() {
		super();
	}

	// Simple CRUD methods

	public Category create() {
		Category result;
		Admin principal;
		Collection<domain.Service> services;
		Collection<Category> childCategories;

		principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);

		result = new Category();
		Assert.notNull(result);
		services = new ArrayList<domain.Service>();
		result.setServices(services);
		childCategories = new ArrayList<Category>();
		result.setChildCategories(childCategories);

		return result;

	}

	public Collection<Category> findAll() {
		Collection<Category> result;
		
	

		result = this.categoryRepository.findAll();

		Assert.notNull(result);

		return result;
	}

	public Category findOne(final int categoryId) {
		Category result;

		result = this.categoryRepository.findOne(categoryId);

		//Assert.notNull(result); Quito el Assert para que si no le paso ninguna categor�a en el controlador con filterCategory me deje hacerlo.

		return result;
	}

	public Category findRootCategory() {
		Category result;

		result = this.categoryRepository.findRootCategory();

		Assert.notNull(result);

		return result;
	}

	public Category save(final Category category) {
		Category result;
		Admin principal;
		Collection<Category> parentCategories;
		Collection<Category> childCategories;
		Category root;

		Assert.notNull(category);

		principal = this.adminService.findByPrincipal();

		Assert.notNull(principal); 

		root = this.findRootCategory();

		Assert.isTrue(category.getId() != root.getId());
		Assert.isTrue(category.getName() != root.getName());
		
		
		result = this.categoryRepository.save(category);
		Assert.notNull(result);

		if (category.getId() != 0) {
			Collection<domain.Service> services;
			services = result.getServices();
			for (final domain.Service service : services)
				service.setCategory(result);
		}

		parentCategories = category.getParentCategories();
		for (final Category c : parentCategories) {
			childCategories = new ArrayList<Category>(c.getChildCategories());
			if (category.getId() != 0)
				childCategories.remove(category);
			childCategories.add(result);
			c.setChildCategories(childCategories);
		}

		childCategories = category.getChildCategories();
		for (final Category c : childCategories) {
			parentCategories = new ArrayList<Category>(c.getParentCategories());
			if (category.getId() != 0)
				parentCategories.remove(category);
			parentCategories.add(result);
			c.setParentCategories(parentCategories);
		}

		return result;

	}

	public void delete(final Category category) {
		Admin principal;

		Collection<Category> childCategories;
		final Collection<Category> parentCategories;
		final Collection<domain.Service> services;
		Category root;

		Assert.notNull(category);
		Assert.isTrue(category.getId() != 0);

		principal = this.adminService.findByPrincipal();

		Assert.notNull(principal); 

		root = this.findRootCategory();

		Assert.isTrue(category.getId() != root.getId());

		childCategories = category.getChildCategories();
		for (final Category child : childCategories) {
			Collection<Category> parents;
			parents = new ArrayList<Category>(child.getParentCategories());
			parents.remove(category);
			if (parents.isEmpty())
				parents.add(this.findRootCategory());
			child.setParentCategories(parents);
		}

		parentCategories = category.getParentCategories();
		for (final Category parent : parentCategories) {
			Collection<Category> children;
			children = new ArrayList<Category>(parent.getParentCategories());
			children.remove(category);
			parent.setParentCategories(children);
		}

		services = category.getServices();
		for (final domain.Service service : services)
			service.setCategory(root);

		this.categoryRepository.delete(category);

	}

	// Other business methods
	public Collection<Category> findChildByParent(final int categoryId) {
		Collection<Category> result;
		result = this.categoryRepository.findChildCategoriesByParent(categoryId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Category> findAllDescendants(final Category ancestor) {
		final Collection<Category> result = new ArrayList<Category>();
		if (ancestor.getId() != 0) {
			Collection<Category> children;
			children = ancestor.getChildCategories();
			for (final Category child : children)
				if (child.getChildCategories().isEmpty())
					result.add(child);
				else {
					result.add(child);
					Collection<Category> grandChildren = new ArrayList<Category>();
					grandChildren = this.findAllDescendants(child);
					result.addAll(grandChildren);
				}
		}
		return result;

	}

	public Collection<Category> findAllAncestors(final Category descendant) {
		final Collection<Category> result = new ArrayList<Category>();
		if (descendant.getId() != 0) {
			Collection<Category> ancestors;
			ancestors = descendant.getParentCategories();
			for (final Category parent : ancestors)
				if (parent.getParentCategories().isEmpty())
					result.add(parent);
				else {
					result.add(parent);
					Collection<Category> grandParents = new ArrayList<Category>();
					grandParents = this.findAllAncestors(parent);
					result.addAll(grandParents);
				}
		}
		return result;

	}

	public Collection<Category> findAllPossibleParentCategories(final Category category) {
		Admin principal;

		Collection<Category> allCategories;
		Collection<Category> descendants;

		principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);

		allCategories = new ArrayList<Category>(this.findAll());

		if (category.getId() != 0) {
			descendants = this.findAllDescendants(category);
			allCategories.remove(category);
			allCategories.removeAll(descendants);
		}

		return allCategories;

	}

}