package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.MPFeeDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import java.util.UUID;

public interface MercadoPagoService {
    Preference createPreference(MPFeeDTO mpFeeDTO) throws MPException, MPApiException;
    String receiveNotification(String topic, String resource, UUID feeId) throws MPException, MPApiException;
}
