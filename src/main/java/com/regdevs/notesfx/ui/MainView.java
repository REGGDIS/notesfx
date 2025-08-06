package com.regdevs.notesfx.ui;

import com.regdevs.notesfx.model.Note;
import com.regdevs.notesfx.storage.NoteRepository;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import org.fxmisc.richtext.CodeArea;

import java.util.List;
import java.util.Set;

public class MainView {

    private final BorderPane root = new BorderPane();
    private final ListView<Note> notesList = new ListView<>();
    private final TextField searchField = new TextField();
    private final TextField titleField = new TextField();
    private final CodeArea editor = new CodeArea();
    private Note current;

    public MainView() {
        // Top toolbar
        Button newBtn = new Button("Nuevo");
        Button saveBtn = new Button("Guardar");
        Button deleteBtn = new Button("Eliminar");

        HBox topBar = new HBox(8, newBtn, saveBtn, deleteBtn,
                new Label(" | Título:"), titleField);
        topBar.setPadding(new Insets(8));
        HBox.setHgrow(titleField, Priority.ALWAYS);

        // Search bar
        searchField.setPromptText("Buscar (#etiqueta o texto)...");
        HBox searchBar = new HBox(8, new Label("Buscar:"), searchField);
        searchBar.setPadding(new Insets(0, 8, 8, 8));

        VBox top = new VBox(topBar, new Separator(), searchBar);
        root.setTop(top);

        // Left: notes list
        notesList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Note item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.title());
            }
        });
        notesList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, sel) -> {
                    if (sel != null) loadNote(sel);
                });
        VBox leftBox = new VBox(new Label("Notas"), notesList);
        leftBox.setPadding(new Insets(8));
        VBox.setVgrow(notesList, Priority.ALWAYS);
        root.setLeft(leftBox);

        // Center: editor area
        editor.setWrapText(true);
        titleField.setPromptText("Título de la nota");
        VBox centerBox = new VBox(new Label("Editor (Markdown)"), editor);
        centerBox.setPadding(new Insets(8));
        VBox.setVgrow(editor, Priority.ALWAYS);
        root.setCenter(centerBox);

        // Actions
        newBtn.setOnAction(e -> newNote());
        saveBtn.setOnAction(e -> saveNote());
        deleteBtn.setOnAction(e -> deleteNote());
        searchField.setOnAction(e -> refreshList());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
                refreshList();
            }
        });

        // Initial load
        refreshList();
    }

    private void refreshList() {
        String q = searchField.getText().trim();
        List<Note> items = NoteRepository.getInstance().search(q);
        notesList.getItems().setAll(items);
        if (!items.isEmpty()) {
            notesList.getSelectionModel().selectFirst();
        } else {
            newNote();
        }
    }

    private void newNote() {
        current = null;
        titleField.clear();
        editor.clear();
        titleField.requestFocus();
    }

    private void loadNote(Note note) {
        current = note;
        titleField.setText(note.title());
        editor.replaceText(note.content());
    }

    private void saveNote() {
        String title = titleField.getText().trim();
        String content = editor.getText();
        if (title.isBlank() && content.isBlank()) return;
        Set<String> tags = Note.extractTags(content);
        if (current == null) {
            current = NoteRepository.getInstance()
                    .create(title, content, tags);
        } else {
            NoteRepository.getInstance()
                    .update(current.id(), title, content, tags);
            current = NoteRepository.getInstance()
                    .findById(current.id());
        }
        refreshList();
        notesList.getSelectionModel().select(current);
    }

    private void deleteNote() {
        if (current != null) {
            NoteRepository.getInstance().delete(current.id());
            newNote();
            refreshList();
        }
    }

    public Parent getRoot() {
        return root;
    }
}
