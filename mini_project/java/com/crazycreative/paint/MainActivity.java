package com.crazycreative.paint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.crazycreative.paint.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseReference databaseReference;
    private boolean firebaseReady = true;
    private View selectedColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebase();
        configureCanvas();
        configureControls();
    }

    private void initFirebase() {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("paintings");
        } catch (Exception exception) {
            firebaseReady = false;
            Toast.makeText(this, "Firebase not initialized", Toast.LENGTH_LONG).show();
        }
    }

    private void configureCanvas() {
        binding.paintView.setStrokeWidth(binding.strokeSeekbar.getProgress());
        binding.paintView.setBrushColor(Color.BLACK);
        binding.paintView.setToolMode(PaintView.ToolMode.BRUSH);
        selectColorCard(binding.colorBlackCard);
        updateToolSelection(binding.buttonBrush);
    }

    private void configureControls() {
        binding.strokeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int safeWidth = Math.max(progress, 1);
                binding.paintView.setStrokeWidth(safeWidth);
                binding.strokeValue.setText(getString(R.string.stroke_value, safeWidth));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.strokeValue.setText(getString(R.string.stroke_value, binding.strokeSeekbar.getProgress()));

        binding.colorBlackCard.setOnClickListener(view -> setBrushColor(Color.BLACK, binding.colorBlackCard));
        binding.colorBlueCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#1565C0"), binding.colorBlueCard));
        binding.colorRedCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#D32F2F"), binding.colorRedCard));
        binding.colorGreenCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#2E7D32"), binding.colorGreenCard));
        binding.colorYellowCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#F9A825"), binding.colorYellowCard));

        binding.buttonBrush.setOnClickListener(view -> {
            binding.paintView.disableEraser();
            binding.paintView.setToolMode(PaintView.ToolMode.BRUSH);
            updateToolSelection(binding.buttonBrush);
            hideAllSubContainers();
        });

        binding.buttonBrushVarieties.setOnClickListener(view -> toggleContainer(binding.brushVarietiesContainer));

        binding.buttonBrushNormal.setOnClickListener(view -> {
            binding.paintView.setNeon(false);
            binding.paintView.setDashed(false);
            hideAllSubContainers();
            updateToolSelection(binding.buttonBrush);
        });

        binding.buttonBrushNeon.setOnClickListener(view -> {
            binding.paintView.setNeon(true);
            hideAllSubContainers();
            updateToolSelection(binding.buttonBrush);
        });

        binding.buttonBrushDashed.setOnClickListener(view -> {
            binding.paintView.setDashed(true);
            hideAllSubContainers();
            updateToolSelection(binding.buttonBrush);
        });

        binding.buttonEraser.setOnClickListener(view -> {
            binding.paintView.enableEraser();
            clearColorSelection();
            updateToolSelection(binding.buttonEraser);
            hideAllSubContainers();
        });

        binding.buttonShapes.setOnClickListener(view -> toggleContainer(binding.shapesContainer));
        binding.buttonColors.setOnClickListener(view -> toggleContainer(binding.colorsContainer));
        binding.buttonEmoji.setOnClickListener(view -> toggleContainer(binding.emojiContainer));
        binding.buttonSaveMenu.setOnClickListener(view -> toggleContainer(binding.exportContainer));

        binding.buttonLine.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.LINE, binding.buttonLine));
        binding.buttonRectangle.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.RECTANGLE, binding.buttonRectangle));
        binding.buttonOval.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.OVAL, binding.buttonOval));
        binding.buttonTriangle.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.TRIANGLE, binding.buttonTriangle));
        binding.buttonStar.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.STAR, binding.buttonStar));
        binding.buttonHeart.setOnClickListener(view -> activateShapeMode(PaintView.ToolMode.HEART, binding.buttonHeart));
        
        setupEmojiPicker();

        binding.buttonUndo.setOnClickListener(view -> binding.paintView.undo());
        binding.buttonRedo.setOnClickListener(view -> binding.paintView.redo());
        binding.buttonClear.setOnClickListener(view -> binding.paintView.clearCanvas());
        
        binding.buttonSavePng.setOnClickListener(view -> saveImage(Bitmap.CompressFormat.PNG, "png"));
        binding.buttonSaveJpg.setOnClickListener(view -> saveImage(Bitmap.CompressFormat.JPEG, "jpg"));
        binding.buttonUploadPng.setOnClickListener(view -> uploadToFirebase(Bitmap.CompressFormat.PNG, "png"));
        binding.buttonUploadJpg.setOnClickListener(view -> uploadToFirebase(Bitmap.CompressFormat.JPEG, "jpg"));
        
        binding.colorBlackCard.setOnClickListener(view -> setBrushColor(Color.BLACK, binding.colorBlackCard));
        binding.colorBlueCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#1565C0"), binding.colorBlueCard));
        binding.colorRedCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#D32F2F"), binding.colorRedCard));
        binding.colorGreenCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#2E7D32"), binding.colorGreenCard));
        binding.colorYellowCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#F9A825"), binding.colorYellowCard));
        binding.colorPurpleCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#9C27B0"), binding.colorPurpleCard));
        binding.colorOrangeCard.setOnClickListener(view -> setBrushColor(Color.parseColor("#FF9800"), binding.colorOrangeCard));
    }

    private void toggleContainer(View container) {
        boolean wasVisible = container.getVisibility() == View.VISIBLE;
        hideAllSubContainers();
        if (!wasVisible) {
            container.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllSubContainers() {
        binding.shapesContainer.setVisibility(View.GONE);
        binding.colorsContainer.setVisibility(View.GONE);
        binding.emojiContainer.setVisibility(View.GONE);
        binding.exportContainer.setVisibility(View.GONE);
        binding.brushVarietiesContainer.setVisibility(View.GONE);
    }

    private void setupEmojiPicker() {
        for (int i = 0; i < binding.emojiList.getChildCount(); i++) {
            View child = binding.emojiList.getChildAt(i);
            if (child instanceof android.widget.TextView) {
                android.widget.TextView emojiView = (android.widget.TextView) child;
                emojiView.setOnClickListener(v -> {
                    binding.paintView.setCurrentEmoji(emojiView.getText().toString());
                    updateToolSelection(binding.buttonEmoji);
                    hideAllSubContainers();
                });
            }
        }
    }

    private void setBrushColor(@ColorInt int color, View selectedView) {
        binding.paintView.setBrushColor(color);
        if (selectedView != null) {
            selectColorCard(selectedView);
        }
        if (binding.paintView.getToolMode() == PaintView.ToolMode.ERASER) {
            binding.paintView.setToolMode(PaintView.ToolMode.BRUSH);
            updateToolSelection(binding.buttonBrush);
        }
    }

    private void activateShapeMode(PaintView.ToolMode mode, View selectedButton) {
        binding.paintView.disableEraser();
        binding.paintView.setToolMode(mode);
        updateToolSelection(selectedButton);
    }

    private void selectColorCard(View selectedView) {
        binding.colorBlackCard.setAlpha(0.45f);
        binding.colorBlueCard.setAlpha(0.45f);
        binding.colorRedCard.setAlpha(0.45f);
        binding.colorGreenCard.setAlpha(0.45f);
        binding.colorYellowCard.setAlpha(0.45f);
        binding.colorPurpleCard.setAlpha(0.45f);
        binding.colorOrangeCard.setAlpha(0.45f);
        selectedView.setAlpha(1f);
        selectedColorView = selectedView;
    }

    private void clearColorSelection() {
        binding.colorBlackCard.setAlpha(0.45f);
        binding.colorBlueCard.setAlpha(0.45f);
        binding.colorRedCard.setAlpha(0.45f);
        binding.colorGreenCard.setAlpha(0.45f);
        binding.colorYellowCard.setAlpha(0.45f);
        binding.colorPurpleCard.setAlpha(0.45f);
        binding.colorOrangeCard.setAlpha(0.45f);
    }

    private void highlightToolButton(View view, boolean selected) {
        view.setAlpha(selected ? 1f : 0.7f);
    }

    private void updateToolSelection(View selectedTool) {
        highlightToolButton(binding.buttonBrush, selectedTool == binding.buttonBrush);
        highlightToolButton(binding.buttonEraser, selectedTool == binding.buttonEraser);
        highlightToolButton(binding.buttonEmoji, selectedTool == binding.buttonEmoji);
        
        // For shapes
        boolean isShape = selectedTool == binding.buttonLine || selectedTool == binding.buttonRectangle || 
                         selectedTool == binding.buttonOval || selectedTool == binding.buttonTriangle || 
                         selectedTool == binding.buttonStar || selectedTool == binding.buttonHeart;
        highlightToolButton(binding.buttonShapes, isShape);
    }

    private void saveImage(Bitmap.CompressFormat format, String extension) {
        Bitmap bitmap = binding.paintView.exportBitmap();
        String savedName = ImageStorage.saveBitmapToGallery(this, bitmap, format, extension);
        if (savedName == null) {
            Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, getString(R.string.save_success, savedName), Toast.LENGTH_LONG).show();
    }

    private void uploadToFirebase(Bitmap.CompressFormat format, String extension) {
        if (!firebaseReady) {
            Toast.makeText(this, R.string.firebase_not_configured, Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bitmap = binding.paintView.exportBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int quality = format == Bitmap.CompressFormat.JPEG ? 92 : 100;
        bitmap.compress(format, quality, stream);
        byte[] imageBytes = stream.toByteArray();

        String fileName = createTimestampName(extension);
        binding.statusText.setText("Encoding and uploading...");

        // Convert image to Base64 string to store in Realtime Database
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", fileName);
        payload.put("format", extension);
        payload.put("imageData", "data:image/" + extension + ";base64," + base64Image);
        payload.put("strokeWidth", binding.paintView.getStrokeWidth());
        payload.put("toolMode", binding.paintView.getToolMode().name());
        payload.put("createdAt", ServerValue.TIMESTAMP);

        databaseReference.push()
                .setValue(payload)
                .addOnSuccessListener(aVoid -> {
                    binding.statusText.setText("Successfully saved to Cloud!");
                    Toast.makeText(this, "Saved to Cloud (Realtime DB)", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(error -> {
                    binding.statusText.setText("Cloud save failed");
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String createTimestampName(String extension) {
        return "paint_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + "." + extension;
    }
}
