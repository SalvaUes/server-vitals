package com.svit.server_vitals.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.svit.server_vitals.model.MetricaHistorial;
import com.svit.server_vitals.repository.MetricaHistorialRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportService {

    private final MetricaHistorialRepository metricaHistorialRepository;

    public PdfReportService(MetricaHistorialRepository metricaHistorialRepository) {
        this.metricaHistorialRepository = metricaHistorialRepository;
    }

    public byte[] generarReportePDF() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Métricas del Sistema")
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());

        Table table = new Table(com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2, 2}))
                .useAllAvailableWidth();

        table.addHeaderCell("Fecha");
        table.addHeaderCell("Hora");
        table.addHeaderCell("CPU (%)");
        table.addHeaderCell("RAM (%)");
        table.addHeaderCell("DISCO (%)");
        table.addHeaderCell("Tipo");

        DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

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

        return outputStream.toByteArray();
    }
}
