package com.warehouse.config;

import com.warehouse.entity.User;
import com.warehouse.entity.Item;
import com.warehouse.entity.Stock;
import com.warehouse.entity.ItemVariant;
import com.warehouse.repository.UserRepository;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.StockRepository;
import com.warehouse.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Auto-seed default users, items, and stock on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemVariantRepository itemVariantRepository;
    private final StockRepository stockRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting data seeding...");

        // Seed Users
        seedUsers();

        // Seed Items, Variants, and Stock
        seedItems();

        log.info("✅ Data seeding completed!");
    }

    private void seedUsers() {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@localhost")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .active(true)
                    .accountLocked(false)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user created - username: admin, password: admin123");
        }

        // Create test user if not exists
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("test@localhost")
                    .password(passwordEncoder.encode("test123"))
                    .role("ROLE_USER")
                    .active(true)
                    .accountLocked(false)
                    .emailVerified(true)
                    .build();
            userRepository.save(testUser);
            log.info("✅ Test user created - username: testuser, password: test123");
        }
    }

    private void seedItems() {
        // Create Item 1: Laptop
        if (!itemRepository.existsBySku("LAP-001")) {
            Item laptop = Item.builder()
                    .name("Laptop Dell XPS 13")
                    .sku("LAP-001")
                    .description("High-performance 13-inch ultrabook laptop")
                    .basePrice("1200.00")
                    .reorderLevel("5")
                    .category("Electronics")
                    .active(true)
                    .build();
            itemRepository.save(laptop);
            
            // Create default variant with priceModifier
            ItemVariant laptopVariant = ItemVariant.builder()
                    .item(laptop)
                    .name("Standard Edition")
                    .sku("LAP-001-STD")
                    .priceModifier(0.0)  // Add default price modifier
                    .active(true)
                    .build();
            itemVariantRepository.save(laptopVariant);
            
            // Add stock for the laptop
            Stock laptopStock = Stock.builder()
                    .item(laptop)
                    .variant(laptopVariant)
                    .quantityOnHand(15)
                    .quantityReserved(0)
                    .quantityAvailable(15)
                    .build();
            stockRepository.save(laptopStock);
            
            log.info("✅ Laptop item created - SKU: LAP-001, Stock: 15");
        }

        // Create Item 2: USB Cable
        if (!itemRepository.existsBySku("CAB-001")) {
            Item cable = Item.builder()
                    .name("USB Cable Type-C")
                    .sku("CAB-001")
                    .description("High-quality USB Type-C charging and data cable")
                    .basePrice("15.00")
                    .reorderLevel("10")
                    .category("Accessories")
                    .active(true)
                    .build();
            itemRepository.save(cable);
            
            // Create default variant with priceModifier
            ItemVariant cableVariant = ItemVariant.builder()
                    .item(cable)
                    .name("1 Meter")
                    .sku("CAB-001-1M")
                    .priceModifier(0.0)  // Add default price modifier
                    .active(true)
                    .build();
            itemVariantRepository.save(cableVariant);
            
            // Add stock for the cable
            Stock cableStock = Stock.builder()
                    .item(cable)
                    .variant(cableVariant)
                    .quantityOnHand(2)  // Below reorder level for testing low stock feature
                    .quantityReserved(0)
                    .quantityAvailable(2)
                    .build();
            stockRepository.save(cableStock);
            
            log.info("✅ USB Cable item created - SKU: CAB-001, Stock: 2 (LOW STOCK)");
        }

        // Create Item 3: Monitor
        if (!itemRepository.existsBySku("MON-001")) {
            Item monitor = Item.builder()
                    .name("Monitor LG 27 Inch 4K")
                    .sku("MON-001")
                    .description("27-inch 4K IPS monitor for professional work")
                    .basePrice("399.99")
                    .reorderLevel("3")
                    .category("Electronics")
                    .active(true)
                    .build();
            itemRepository.save(monitor);
            
            // Create default variant with priceModifier
            ItemVariant monitorVariant = ItemVariant.builder()
                    .item(monitor)
                    .name("Standard Edition")
                    .sku("MON-001-STD")
                    .priceModifier(0.0)  // Add default price modifier
                    .active(true)
                    .build();
            itemVariantRepository.save(monitorVariant);
            
            // Add stock for the monitor
            Stock monitorStock = Stock.builder()
                    .item(monitor)
                    .variant(monitorVariant)
                    .quantityOnHand(8)
                    .quantityReserved(0)
                    .quantityAvailable(8)
                    .build();
            stockRepository.save(monitorStock);
            
            log.info("✅ Monitor item created - SKU: MON-001, Stock: 8");
        }

        // Create Item 4: Keyboard
        if (!itemRepository.existsBySku("KEY-001")) {
            Item keyboard = Item.builder()
                    .name("Mechanical Keyboard RGB")
                    .sku("KEY-001")
                    .description("Mechanical keyboard with RGB backlighting")
                    .basePrice("149.99")
                    .reorderLevel("5")
                    .category("Accessories")
                    .active(true)
                    .build();
            itemRepository.save(keyboard);
            
            // Create default variant with priceModifier
            ItemVariant keyboardVariant = ItemVariant.builder()
                    .item(keyboard)
                    .name("Standard Edition")
                    .sku("KEY-001-STD")
                    .priceModifier(0.0)  // Add default price modifier
                    .active(true)
                    .build();
            itemVariantRepository.save(keyboardVariant);
            
            // Add stock for the keyboard
            Stock keyboardStock = Stock.builder()
                    .item(keyboard)
                    .variant(keyboardVariant)
                    .quantityOnHand(0)  // Out of stock for testing
                    .quantityReserved(0)
                    .quantityAvailable(0)
                    .build();
            stockRepository.save(keyboardStock);
            
            log.info("✅ Keyboard item created - SKU: KEY-001, Stock: 0 (OUT OF STOCK)");
        }

        // Create Item 5: Mouse
        if (!itemRepository.existsBySku("MOU-001")) {
            Item mouse = Item.builder()
                    .name("Wireless Mouse Logitech")
                    .sku("MOU-001")
                    .description("Wireless mouse with precision tracking")
                    .basePrice("49.99")
                    .reorderLevel("10")
                    .category("Accessories")
                    .active(true)
                    .build();
            itemRepository.save(mouse);
            
            // Create default variant with priceModifier
            ItemVariant mouseVariant = ItemVariant.builder()
                    .item(mouse)
                    .name("Standard Edition")
                    .sku("MOU-001-STD")
                    .priceModifier(0.0)  // Add default price modifier
                    .active(true)
                    .build();
            itemVariantRepository.save(mouseVariant);
            
            // Add stock for the mouse
            Stock mouseStock = Stock.builder()
                    .item(mouse)
                    .variant(mouseVariant)
                    .quantityOnHand(25)
                    .quantityReserved(0)
                    .quantityAvailable(25)
                    .build();
            stockRepository.save(mouseStock);
            
            log.info("✅ Mouse item created - SKU: MOU-001, Stock: 25");
        }

        log.info("✅ All items, variants, and stock seeded successfully!");
    }
}
