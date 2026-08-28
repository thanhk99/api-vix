package vix.local.api.modules.worker.application.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vix.local.api.modules.capital_source.domain.model.Partner;
import vix.local.api.modules.capital_source.domain.repository.PartnerRepository;
import vix.local.api.modules.worker.domain.model.WorkerJob;
import vix.local.api.modules.worker.domain.repository.WorkerJobRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExportWorker {

    private final WorkerJobRepository workerJobRepository;
    private final PartnerRepository partnerRepository;

    private static final String EXPORT_DIR = "uploads/exports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processExportJobs() {
        List<WorkerJob> jobs = workerJobRepository.findPendingExportJobs(LocalDateTime.now(), 5);
        if (jobs.isEmpty()) {
            return;
        }

        for (WorkerJob job : jobs) {
            job.markProcessing();
            workerJobRepository.save(job);

            try {
                log.info("Processing export job: {} of type: {}", job.getId(), job.getJobType());

                Path exportPath = Paths.get(EXPORT_DIR);
                if (!Files.exists(exportPath)) {
                    Files.createDirectories(exportPath);
                }

                String generatedFileName;
                File targetFile;

                if ("EXPORT_PARTNER".equalsIgnoreCase(job.getJobType())) {
                    generatedFileName = "Danh_sach_doi_tac_" + System.currentTimeMillis() + ".xlsx";
                    targetFile = exportPath.resolve(job.getId() + "_" + generatedFileName).toFile();
                    exportPartnersToExcel(targetFile);
                } else {
                    // Generic export handler
                    generatedFileName = (job.getFileName() != null ? job.getFileName() : "Export_" + job.getJobType()) + "_" + System.currentTimeMillis() + ".xlsx";
                    targetFile = exportPath.resolve(job.getId() + "_" + generatedFileName).toFile();
                    exportGenericToExcel(targetFile, job);
                }

                job.markCompleted(targetFile.getAbsolutePath(), generatedFileName, targetFile.length());
                log.info("Successfully completed export job: {}, saved to: {}", job.getId(), targetFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("Failed to process export job: {}", job.getId(), e);
                job.markFailed(e.getMessage() != null ? e.getMessage() : e.toString(), 3);
            }

            workerJobRepository.save(job);
        }
    }

    private void exportPartnersToExcel(File targetFile) throws Exception {
        Page<Partner> partnerPage = partnerRepository.findAll(Pageable.unpaged());
        List<Partner> allPartners = partnerPage.getContent().stream()
                .filter(p -> p != null && !Partner.STATUS_DRAFT.equalsIgnoreCase(p.getStatus()) 
                        && (p.getCusId() == null || !p.getCusId().startsWith("DRAFT_"))
                        && (p.getCusName() == null || !p.getCusName().equalsIgnoreCase("Bản nháp")))
                .toList();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách đối tác");

            // Fonts
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            Font contentFont = workbook.createFont();
            contentFont.setFontHeightInPoints((short) 11);

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Content Styles
            CellStyle leftStyle = workbook.createCellStyle();
            leftStyle.setFont(contentFont);
            leftStyle.setAlignment(HorizontalAlignment.LEFT);
            leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            leftStyle.setBorderTop(BorderStyle.THIN);
            leftStyle.setBorderBottom(BorderStyle.THIN);
            leftStyle.setBorderLeft(BorderStyle.THIN);
            leftStyle.setBorderRight(BorderStyle.THIN);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setFont(contentFont);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerStyle.setBorderTop(BorderStyle.THIN);
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);

            // Header columns matching specification image:
            // STT | Mã KH | Đơn vị GD | Tên KH | Số ĐKKH/CCCD | Ngày cấp lần đầu | Ngày cấp cuối | Nơi cấp | GPHD | Ngày cấp
            String[] headers = {
                    "STT",
                    "Mã KH",
                    "Đơn vị GD",
                    "Tên KH",
                    "Số ĐKKH/CCCD",
                    "Ngày cấp lần đầu",
                    "Ngày cấp cuối",
                    "Nơi cấp",
                    "GPHD",
                    "Ngày cấp"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIndex = 1;
            for (Partner p : allPartners) {
                Row row = sheet.createRow(rowIndex);
                row.setHeightInPoints(20);

                // 0: STT
                Cell c0 = row.createCell(0);
                c0.setCellValue(rowIndex);
                c0.setCellStyle(centerStyle);

                // 1: Mã KH
                Cell c1 = row.createCell(1);
                c1.setCellValue(p.getCusId() != null ? p.getCusId() : "");
                c1.setCellStyle(leftStyle);

                // 2: Đơn vị GD
                Cell c2 = row.createCell(2);
                c2.setCellValue(p.getBranchCusId() != null ? p.getBranchCusId() : "");
                c2.setCellStyle(leftStyle);

                // 3: Tên KH
                Cell c3 = row.createCell(3);
                c3.setCellValue(p.getCusName() != null ? p.getCusName() : "");
                c3.setCellStyle(leftStyle);

                // 4: Số ĐKKH/CCCD
                Cell c4 = row.createCell(4);
                c4.setCellValue(p.getIdCode() != null ? p.getIdCode() : "");
                c4.setCellStyle(leftStyle);

                // 5: Ngày cấp lần đầu
                Cell c5 = row.createCell(5);
                c5.setCellValue(formatDate(p.getFistIssueDate()));
                c5.setCellStyle(centerStyle);

                // 6: Ngày cấp cuối
                Cell c6 = row.createCell(6);
                c6.setCellValue(formatDate(p.getLastIssueDate()));
                c6.setCellStyle(centerStyle);

                // 7: Nơi cấp
                Cell c7 = row.createCell(7);
                c7.setCellValue(p.getIssueBy() != null ? p.getIssueBy() : "");
                c7.setCellStyle(leftStyle);

                // 8: GPHD
                Cell c8 = row.createCell(8);
                c8.setCellValue(p.getOpLiscenseNo() != null ? p.getOpLiscenseNo() : "");
                c8.setCellStyle(leftStyle);

                // 9: Ngày cấp
                Cell c9 = row.createCell(9);
                c9.setCellValue(formatDate(p.getOpIssueDate()));
                c9.setCellStyle(centerStyle);

                rowIndex++;
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Extra padding
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(currentWidth + 1200, 3000));
            }

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }

    private void exportGenericToExcel(File targetFile, WorkerJob job) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Export");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Export Data: " + job.getJobType());
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                workbook.write(fos);
            }
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DATE_FORMATTER);
    }
}
