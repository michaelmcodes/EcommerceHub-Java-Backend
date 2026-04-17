package com.michaelmark.ecommerce.service;

import com.michaelmark.ecommerce.exceptions.APIException;
import com.michaelmark.ecommerce.exceptions.ResourceNotFoundException;
import com.michaelmark.ecommerce.model.Category;
import com.michaelmark.ecommerce.model.Product;
import com.michaelmark.ecommerce.payload.ProductDTO;
import com.michaelmark.ecommerce.payload.ProductResponse;
import com.michaelmark.ecommerce.repositories.CategoryRepository;
import com.michaelmark.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Product product = modelMapper.map(productDTO, Product.class);

        // Check if product is already present or not
        List<Product> products = category.getProducts();
        for(Product thisProduct : products){
            if(Objects.equals(product.getProductName(), thisProduct.getProductName())){
                throw new APIException("Product already exists");
            }
        }

        product.setCategory(category);
        product.setImage("default.png");
        Double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());

        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        // Check if product size is 0 or not
        List<Product> products = pageProducts.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(pageProducts.getNumber());
        response.setPageSize(pageProducts.getSize());
        response.setTotalPages(pageProducts.getTotalPages());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setLastPage(pageProducts.isLast());
        return response;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Check if product size is 0 or not
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        // Check if product size is 0 or not
        List<Product> productsList = pageProducts.getContent();
        if(productsList.isEmpty()){
            throw new APIException("Category with category name " + category.getCategoryName() + " does not have any products");
        }


        List<ProductDTO> productDTOS = productsList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(pageProducts.getNumber());
        response.setPageSize(pageProducts.getSize());
        response.setTotalPages(pageProducts.getTotalPages());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setLastPage(pageProducts.isLast());

        return response;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);

        List<Product> productsList = pageProducts.getContent();
        if(productsList.isEmpty()){
            throw new APIException("Products not found with keyword: " + keyword);
        }
        List<ProductDTO> productDTOS = productsList.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        response.setPageNumber(pageProducts.getNumber());
        response.setPageSize(pageProducts.getSize());
        response.setTotalPages(pageProducts.getTotalPages());
        response.setTotalElements(pageProducts.getTotalElements());
        response.setLastPage(pageProducts.isLast());

        return response;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId){
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductID", productId));
        productFromDb.setProductId(productId);
        productFromDb.setProductName(productDTO.getProductName());
        productFromDb.setDescription(productDTO.getDescription());
        productFromDb.setPrice(productDTO.getPrice());
        productFromDb.setSpecialPrice(productDTO.getSpecialPrice());

        Product product = productRepository.save(productFromDb);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get product from DB
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload image to server
        // Get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // Updating the new file name to the product
        productFromDB.setImage(fileName);

        // Save updated product
        Product updatedProduct = productRepository.save(productFromDB);

        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
