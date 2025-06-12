package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.FeeDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.MPFeeDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.FeeRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.FeeService;
import com.cuu.backend.disciplinas_service.Services.Interfaces.MercadoPagoService;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MercadoPagoServiceImpl implements MercadoPagoService {

    private FeeRepo feeRepo;
    private CategoryRepo categoryRepo;
    private FeeService feeService;
    private DisciplineRepo disciplineRepo;
    private final PreferenceClient preferenceClient;
    private final MerchantOrderClient merchantOrderClient;
    private static final String CURRENCY = "ARS";

    @Value("${back.url}")
    private String BACK_URL;

    @Override
    @Transactional
    public Preference createPreference(MPFeeDTO mpFeeDTO) throws MPException, MPApiException {
        Optional<Fee> feeOpt = Optional.empty();
        Fee fee;

        if (mpFeeDTO.getFeeType().equals(FeeType.SOCIAL)){
            feeOpt = feeRepo.findByUserKeycloakIdAndFeeTypeAndPeriod(mpFeeDTO.getUserKeycloakId(), FeeType.SOCIAL, YearMonth.parse(mpFeeDTO.getPeriod()));

        } else if (mpFeeDTO.getFeeType().equals(FeeType.DISCIPLINE)) {
            feeOpt = feeRepo.findByUserKeycloakIdAndFeeTypeAndDisciplineIdAndPeriod(mpFeeDTO.getUserKeycloakId(), FeeType.DISCIPLINE, mpFeeDTO.getDisciplineId(), YearMonth.parse(mpFeeDTO.getPeriod()));
        }

        if (feeOpt.isEmpty()) {
            throw new CustomException("Cuota no encontrada", HttpStatus.NOT_FOUND);
        }

        if (feeOpt.get().isPaid()) {
            throw new CustomException("Cuota ya pagada", HttpStatus.NOT_FOUND);
        }

        fee = feeOpt.get();

        PreferenceItemRequest preferenceItemRequest = PreferenceItemRequest.builder()
                .title(getFeeTitle(mpFeeDTO, fee))
                .description(fee.getDescription())
                .quantity(1)
                .unitPrice(fee.getAmount())
                .currencyId(CURRENCY)
                .build();

        //TODO URL MODIFICAR
        PreferenceBackUrlsRequest preferenceBackUrlsRequest = PreferenceBackUrlsRequest.builder()
                .success("https://450a-177-37-40-2.ngrok-free.app/mis-cuotas") //TODO CAMBIAR POR MIS URL NGROK HTTPS DEL FRONT
                .pending("https://450a-177-37-40-2.ngrok-free.app/mis-cuotas") //TODO CAMBIAR POR MIS URL NGROK HTTPS DEL FRONT
                .failure("https://450a-177-37-40-2.ngrok-free.app/mis-cuotas") //TODO CAMBIAR POR MIS URL NGROK HTTPS DEL FRONT
                .build();

        String notificationUrl = BACK_URL + "mercado-pago/" + fee.getId();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(preferenceItemRequest))
                .backUrls(preferenceBackUrlsRequest)
                .notificationUrl(notificationUrl)
                .autoReturn("approved")
                .build();


        return preferenceClient.create(preferenceRequest);
    }

    private String getFeeTitle(MPFeeDTO mpFeeDTO, Fee fee) {
        String title = "{title}";

        if (fee.getFeeType().equals(FeeType.SOCIAL)){
            title = "Cuota Social período " + mpFeeDTO.getPeriod();
        }
        else if (fee.getFeeType().equals(FeeType.DISCIPLINE)){
            Optional<Discipline> disciplineOpt = disciplineRepo.findById(fee.getDisciplineId());
            Optional<Category> categoryOpt = categoryRepo.findById(fee.getCategoryId());
            String disciplineName = "{nombre_disc}";
            String categoryName = "{nombre_cat}";

            if (disciplineOpt.isPresent()){
                disciplineName = disciplineOpt.get().getName();
            }
            if (categoryOpt.isPresent()){
                categoryName = categoryOpt.get().getName();
            }

            title = "Cuota de " + disciplineName + " " + categoryName + " período " + mpFeeDTO.getPeriod();
        }
        return title;
    }

    @Override
    @Transactional
    public String receiveNotification(String topic, String resource, UUID feeId) throws MPException, MPApiException {

        String resp = "Notification Recibida";

        if ("merchant_order".equalsIgnoreCase(topic) && resource != null) {
            String[] resourceParts = resource.split("/");
            Long merchantOrderId = Long.valueOf(resourceParts[resourceParts.length - 1]);
            MerchantOrder merchantOrder = this.merchantOrderClient.get(merchantOrderId);

            resp = resp + " | se recibio el merchant_order de MP";

            if ("paid".equalsIgnoreCase(merchantOrder.getOrderStatus())) {
                //marcar la cuota como pagada y generarle PaymentProof
                boolean feeIsPaid = feeService.MPUpdateFeePaidState(merchantOrderId, feeId);
                resp = feeIsPaid ? resp + " | Cuota marcada como pagada" : resp + " | Cuota NO se marco como pagada";
            }
        }

        return resp;
    }
}
