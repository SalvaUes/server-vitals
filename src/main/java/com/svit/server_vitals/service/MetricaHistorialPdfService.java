package com.svit.server_vitals.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.svit.server_vitals.model.MetricaHistorial;
import com.svit.server_vitals.repository.MetricaHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MetricaHistorialPdfService {

    @Autowired
    private MetricaHistorialRepository metricaHistorialRepository;

    public void generarReportePdf(OutputStream outputStream) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);
        document.open();

        document.add(new Paragraph("Reporte de Métricas de Sistema", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph(" ")); // Espacio

        PdfPTable table = new PdfPTable(6);
        table.setWidths(new float[]{2, 2, 2, 2, 2, 2});
        table.addCell("Fecha");
        table.addCell("Hora");
        table.addCell("CPU (%)");
        table.addCell("RAM (%)");
        table.addCell("Disco (%)");
        table.addCell("Nivel Alerta");

        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        List<MetricaHistorial> metricas = metricaHistorialRepository.findAll();

        for (MetricaHistorial metrica : metricas) {
            table.addCell(metrica.getFechaHora().format(fechaFormatter));
            table.addCell(metrica.getFechaHora().format(horaFormatter));
            table.addCell(String.format("%.2f", metrica.getUsoCpu()));
            table.addCell(String.format("%.2f", metrica.getUsoRam()));
            table.addCell(String.format("%.2f", metrica.getUsoDisco()));
            table.addCell(metrica.getNivelAlerta().toString());
        }

        document.add(table);
        document.close();
    }
}
