package com.market_view.spring.mv.service;

import com.market_view.spring.mv.model.Market;
import com.market_view.spring.mv.repository.MarketRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketServiceTest {

    @Mock
    private MarketRepository marketRepository;

    @InjectMocks
    private MarketService marketService;

    private Market sampleMarket;

    @BeforeEach
    void setUp() {
        sampleMarket = new Market();
        sampleMarket.setId("1");
        sampleMarket.setName("NYSE");
        sampleMarket.setDescription("New York Stock Exchange");
    }

    @Test
    void testGetAllMarkets() {
        when(marketRepository.findAll()).thenReturn(List.of(sampleMarket));

        List<Market> result = marketService.getAllMarkets();
        assertEquals(1, result.size());
        assertEquals("NYSE", result.get(0).getName());
    }

    @Test
    void testGetMarketById_Found() {
        when(marketRepository.findById("1")).thenReturn(Optional.of(sampleMarket));

        Market result = marketService.getMarketById("1");
        assertEquals("NYSE", result.getName());
    }

    @Test
    void testGetMarketById_NotFound() {
        when(marketRepository.findById("2")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> marketService.getMarketById("2"));
        assertEquals("Market not found", ex.getMessage());
    }

    @Test
    void testCreateMarket() {
        when(marketRepository.save(sampleMarket)).thenReturn(sampleMarket);

        Market result = marketService.createMarket(sampleMarket);
        assertEquals("NYSE", result.getName());
    }

    @Test
    void testUpdateMarket() {
        Market updated = new Market();
        updated.setName("Updated NYSE");
        updated.setDescription("Updated description");

        when(marketRepository.findById("1")).thenReturn(Optional.of(sampleMarket));
        when(marketRepository.save(any(Market.class))).thenReturn(sampleMarket);

        Market result = marketService.updateMarket("1", updated);
        assertEquals("Updated NYSE", result.getName());
        assertEquals("Updated description", result.getDescription());
    }

    @Test
    void testDeleteMarket() {
        when(marketRepository.findById("1")).thenReturn(Optional.of(sampleMarket));
        doNothing().when(marketRepository).delete(sampleMarket);

        assertDoesNotThrow(() -> marketService.deleteMarket("1"));
        verify(marketRepository).delete(sampleMarket);
    }
}
