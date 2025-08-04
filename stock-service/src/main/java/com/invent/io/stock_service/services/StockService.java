package com.invent.io.stock_service.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.invent.io.stock_service.enums.MovementType;
import com.invent.io.stock_service.model.Stock;
import com.invent.io.stock_service.model.StockMovement;
import com.invent.io.stock_service.repository.StockMovementRepository;
import com.invent.io.stock_service.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    //* Adiciona estoque para um produto específico
    public void addStock(String skuCode, int quantity, String description) {
        Stock stock = stockRepository.findBySkuCode(skuCode)
                .orElseGet(() -> new Stock(null, skuCode, 0, 0, 0));

        stock.setQuantity_total(stock.getQuantity_total() + quantity);
        stock.setQuantity_available(stock.getQuantity_available() + quantity);

        stockRepository.save(stock);
        recordMovement(skuCode, MovementType.IN, quantity, description);
    }

    //* Remove estoque para um produto específico
    public void decrementStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantity_available(), quantity, skuCode, "disponível");

        stock.setQuantity_available(stock.getQuantity_available() - quantity);
        stock.setQuantity_total(stock.getQuantity_total() - quantity);

        stockRepository.save(stock);
        recordMovement(skuCode, MovementType.OUT, quantity, "Saída de estoque");
    }

    //* Valida se existe estoque disponível para um produto específico
    public boolean isInStock(String skuCode, int quantity) {
        return stockRepository.existsBySkuCodeAndQuantityAvailableIsGreaterThanEqual(skuCode, quantity);
    }

    //* Reserva estoque para um produto específico
    public void reserveStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantity_available(), quantity, skuCode, "disponível");

        stock.setQuantity_available(stock.getQuantity_available() - quantity);
        stock.setQuantity_reserved(stock.getQuantity_reserved() + quantity);

        stockRepository.save(stock);
        recordMovement(skuCode, MovementType.RESERVED, quantity, "Reserva de estoque");
    }

    //* Cancela a reserva do estoque para um produto específico
    public void releaseStock(String skuCode, int quantity) {
        Stock stock = getStockOrThrow(skuCode);
        validateQuantity(stock.getQuantity_reserved(), quantity, skuCode, "reservada");

        stock.setQuantity_reserved(stock.getQuantity_reserved() - quantity);
        stock.setQuantity_available(stock.getQuantity_available() + quantity);

        stockRepository.save(stock);
        recordMovement(skuCode, MovementType.CANCELED, quantity, "Liberação de estoque");
    }

    //* Valida se existe produto com aquele skuCode
    private Stock getStockOrThrow(String skuCode) {
        return stockRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado para o SKU: " + skuCode));
    }

    //* Valida a quantidade disponível para uma operação de estoque
    private void validateQuantity(int available, int required, String skuCode, String tipo) {
        if (available < required) {
            throw new IllegalArgumentException("Quantidade " + tipo + " insuficiente para SKU: " + skuCode);
        }
    }

    //* Salva a movimentação de estoque para histórico e auditoria
    private void recordMovement(String skuCode, MovementType type, int quantity, String description) {
        StockMovement movement = new StockMovement();
        movement.setSkuCode(skuCode);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setDescription(description);
        movement.setTimestamp(LocalDateTime.now());

        stockMovementRepository.save(movement);
    }

}