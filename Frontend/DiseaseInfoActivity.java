package com.example.myapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DiseaseInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_info);

        // Initialize toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        // Setup RecyclerView
        RecyclerView diseaseRecyclerView = findViewById(R.id.diseaseRecyclerView);
        diseaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set adapter with disease data
        DiseaseAdapter diseaseAdapter = new DiseaseAdapter(createDiseaseList());
        diseaseRecyclerView.setAdapter(diseaseAdapter);
    }

    private List<Disease> createDiseaseList() {
        List<Disease> diseases = new ArrayList<>();

        // Acne
        List<String> acneSymptoms = new ArrayList<>();
        acneSymptoms.add("• Whiteheads (closed plugged pores)");
        acneSymptoms.add("• Blackheads (open plugged pores)");
        acneSymptoms.add("• Small red, tender bumps (papules)");
        acneSymptoms.add("• Pimples (pustules)");
        acneSymptoms.add("• Large, solid, painful lumps beneath the skin (nodules)");
        acneSymptoms.add("• Painful, pus-filled lumps beneath the skin (cystic lesions)");

        List<String> acneTreatments = new ArrayList<>();
        acneTreatments.add("✓ Topical treatments like benzoyl peroxide or salicylic acid");
        acneTreatments.add("✓ Antibiotics to reduce bacteria and inflammation");
        acneTreatments.add("✓ Oral contraceptives for hormonal acne in women");
        acneTreatments.add("✓ Isotretinoin for severe acne");
        acneTreatments.add("✓ Light therapy to reduce bacteria");
        acneTreatments.add("✓ Chemical peels to improve mild acne");

        diseases.add(new Disease(
                "Acne",
                "acne.jpg",
                "Acne is a skin condition that occurs when hair follicles become plugged with oil and dead skin cells. It causes whiteheads, blackheads or pimples.",
                acneSymptoms,
                acneTreatments
        ));

        // Eczema
        List<String> eczemaSymptoms = new ArrayList<>();
        eczemaSymptoms.add("• Dry, sensitive skin");
        eczemaSymptoms.add("• Red, inflamed skin");
        eczemaSymptoms.add("• Severe itching");
        eczemaSymptoms.add("• Dark colored patches");
        eczemaSymptoms.add("• Rough, leathery or scaly patches");
        eczemaSymptoms.add("• Oozing or crusting");
        eczemaSymptoms.add("• Areas of swelling");

        List<String> eczemaTreatments = new ArrayList<>();
        eczemaTreatments.add("✓ Moisturize regularly with fragrance-free creams");
        eczemaTreatments.add("✓ Use mild, non-soap cleansers");
        eczemaTreatments.add("✓ Apply corticosteroid creams to reduce inflammation");
        eczemaTreatments.add("✓ Take antihistamines for severe itching");
        eczemaTreatments.add("✓ Use wet dressings for severe flares");
        eczemaTreatments.add("✓ Phototherapy for widespread eczema");

        diseases.add(new Disease(
                "Eczema",
                "eczema.jpg",
                "Eczema is a condition that makes your skin inflamed or irritated. The most common type is atopic dermatitis which often begins in childhood.",
                eczemaSymptoms,
                eczemaTreatments
        ));

        // Add more diseases as needed...

        return diseases;
    }

    // Disease model class
    private static class Disease {
        private final String name;
        private final String image;
        private final String description;
        private final List<String> symptoms;
        private final List<String> treatments;

        public Disease(String name, String image, String description,
                       List<String> symptoms, List<String> treatments) {
            this.name = name;
            this.image = image;
            this.description = description;
            this.symptoms = symptoms;
            this.treatments = treatments;
        }

        public String getName() { return name; }
        public String getImage() { return image; }
        public String getDescription() { return description; }
        public List<String> getSymptoms() { return symptoms; }
        public List<String> getTreatments() { return treatments; }
    }

    // Adapter class
    private class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder> {
        private final List<Disease> diseases;

        public DiseaseAdapter(List<Disease> diseases) {
            this.diseases = diseases;
        }

        @Override
        public DiseaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_disease, parent, false);
            return new DiseaseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DiseaseViewHolder holder, int position) {
            Disease disease = diseases.get(position);

            holder.diseaseName.setText(disease.getName());
            holder.diseaseDescription.setText(disease.getDescription());

            // For images, you would typically use Glide or Picasso
            // Glide.with(holder.itemView.getContext()).load(disease.getImage()).into(holder.diseaseImage);

            // Build symptoms text
            StringBuilder symptomsText = new StringBuilder();
            for (String symptom : disease.getSymptoms()) {
                symptomsText.append(symptom).append("\n");
            }
            holder.symptomsList.setText(symptomsText.toString());

            // Build treatments text
            StringBuilder treatmentsText = new StringBuilder();
            for (String treatment : disease.getTreatments()) {
                treatmentsText.append(treatment).append("\n");
            }
            holder.treatmentsList.setText(treatmentsText.toString());
        }

        @Override
        public int getItemCount() {
            return diseases.size();
        }

        // ViewHolder class
        class DiseaseViewHolder extends RecyclerView.ViewHolder {
            ImageView diseaseImage;
            TextView diseaseName;
            TextView diseaseDescription;
            TextView symptomsList;
            TextView treatmentsList;

            public DiseaseViewHolder(View itemView) {
                super(itemView);
                diseaseImage = itemView.findViewById(R.id.diseaseImage);
                diseaseName = itemView.findViewById(R.id.diseaseName);
                diseaseDescription = itemView.findViewById(R.id.diseaseDescription);
                symptomsList = itemView.findViewById(R.id.symptomsList);
                treatmentsList = itemView.findViewById(R.id.treatmentsList);
            }
        }
    }
}