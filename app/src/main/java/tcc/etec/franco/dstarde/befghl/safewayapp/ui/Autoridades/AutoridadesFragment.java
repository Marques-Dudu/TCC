package tcc.etec.franco.dstarde.befghl.safewayapp.ui.Autoridades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tcc.etec.franco.dstarde.befghl.safewayapp.R;

public class AutoridadesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Authority> authorityList;

    public AutoridadesFragment() {
        // Construtor vazio obrigatório
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_autoridades, container, false);

        recyclerView = view.findViewById(R.id.authorityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        authorityList = new ArrayList<>();
        authorityList.add(new Authority(
                "Polícia Militar",
                R.drawable.policia,
                "190",
                "Responsável pelo patrulhamento ostensivo e pela preservação da ordem pública.\n\n" +
                        "Deve ser acionada em casos de crimes em andamento, como:\n" +
                        "   ● Assaltos ou furtos\n" +
                        "   ● Violência doméstica\n" +
                        "   ● Brigas ou tumultos\n" +
                        "   ● Situações suspeitas ou ameaças à segurança"
        ));

        authorityList.add(new Authority(
                "Corpo de Bombeiros",
                R.drawable.bombeiro,
                "193",
                "Atua em emergências que envolvem riscos físicos e ambientais.\n\n" +
                        "Deve ser chamado em casos de:\n" +
                        "   ● Incêndios\n" +
                        "   ● Acidentes com vítimas presas\n" +
                        "   ● Vazamentos de gás\n" +
                        "   ● Desabamentos ou alagamentos\n" +
                        "   ● Resgates em altura ou locais de difícil acesso"
        ));

        authorityList.add(new Authority(
                "SAMU",
                R.drawable.samu,
                "192",
                "Serviço de Atendimento Móvel de Urgência, especializado em suporte médico imediato.\n\n" +
                        "Ligue para o SAMU em situações como:\n" +
                        "   ● Paradas cardíacas\n" +
                        "   ● Acidentes com feridos\n" +
                        "   ● Dificuldade respiratória\n" +
                        "   ● Desmaios ou perda de consciência\n" +
                        "   ● Crises convulsivas"
        ));

        authorityList.add(new Authority(
                "Polícia Civil",
                R.drawable.swat_svgrepo_com,
                "197",
                "Responsável por investigações criminais e atendimento de ocorrências não emergenciais.\n\n" +
                        "Deve ser procurada em situações como:\n" +
                        "   ● Registro de boletins de ocorrência (B.O.)\n" +
                        "   ● Investigações de crimes\n" +
                        "   ● Casos de estelionato, roubo, homicídio ou desaparecimento\n" +
                        "   ● Denúncias que precisam de apuração"
        ));

        authorityList.add(new Authority(
                "Polícia Rodoviária Federal (PRF)",
                R.drawable.prf,
                "191",
                "Atua na fiscalização e segurança das rodovias federais.\n\n" +
                        "Ligue para a PRF em casos de:\n" +
                        "   ● Acidentes em rodovias federais\n" +
                        "   ● Veículos abandonados ou suspeitos nas estradas\n" +
                        "   ● Transporte de cargas perigosas\n" +
                        "   ● Crimes como tráfico de drogas ou armas nas rodovias"
        ));

        authorityList.add(new Authority(
                "Polícia Rodoviária Estadual",
                R.drawable.pre, // Substitua pelo drawable correto
                "198",
                "Responsável pela fiscalização e segurança nas rodovias estaduais.\n\n" +
                        "Ligue para a Polícia Rodoviária Estadual em casos de:\n" +
                        "   ● Acidentes em rodovias estaduais\n" +
                        "   ● Veículos abandonados ou suspeitos nas estradas\n" +
                        "   ● Irregularidades no transporte\n" +
                        "   ● Situações que comprometam a segurança viária"
        ));

        authorityList.add(new Authority(
                "Defesa Civil",
                R.drawable.defes, // Substitua pelo drawable correto
                "199",
                "Atua na prevenção e resposta a desastres naturais e emergências públicas.\n\n" +
                        "Acione a Defesa Civil em situações como:\n" +
                        "   ● Alagamentos e enchentes\n" +
                        "   ● Deslizamentos de terra\n" +
                        "   ● Riscos de desabamento\n" +
                        "   ● Outras emergências ambientais"
        ));

        authorityList.add(new Authority(
                "Polícia Federal",
                R.drawable.pf, // Substitua pelo drawable correto
                "194",
                "Responsável por investigar crimes federais e atuar em áreas como imigração e segurança nacional.\n\n" +
                        "Entre em contato com a Polícia Federal para:\n" +
                        "   ● Questões de passaporte e imigração\n" +
                        "   ● Crimes cibernéticos e financeiros\n" +
                        "   ● Tráfico internacional de drogas ou armas\n" +
                        "   ● Investigação de organizações criminosas"
        ));

        authorityList.add(new Authority(
                "Disque Direitos Humanos",
                R.drawable.dh, // Substitua pelo drawable correto
                "100",
                "Canal para denúncias de violações dos direitos humanos.\n\n" +
                        "Utilize o Disque 100 para denunciar:\n" +
                        "   ● Abusos contra crianças, adolescentes ou idosos\n" +
                        "   ● Violência contra pessoas com deficiência\n" +
                        "   ● Violação de direitos em instituições públicas ou privadas\n" +
                        "   ● Qualquer forma de discriminação"
        ));

        authorityList.add(new Authority(
                "Central de Atendimento à Mulher",
                R.drawable.mulher, // Substitua pelo drawable correto
                "180",
                "Canal exclusivo de apoio e orientação para mulheres vítimas de violência.\n\n" +
                        "Ligue para o 180 em casos de:\n" +
                        "   ● Agressões físicas ou verbais\n" +
                        "   ● Ameaças, perseguições ou abusos\n" +
                        "   ● Informações sobre seus direitos\n" +
                        "   ● Encaminhamento para serviços de proteção"
        ));

        authorityList.add(new Authority(
                "CVV – Centro de Valorização da Vida",
                R.drawable.cvv, // Substitua pelo drawable correto
                "188",
                "Oferece apoio emocional e prevenção do suicídio, de forma gratuita e sigilosa.\n\n" +
                        "Entre em contato com o CVV se você ou alguém que conhece:\n" +
                        "   ● Estiver em sofrimento emocional\n" +
                        "   ● Precisar conversar com alguém\n" +
                        "   ● Estiver passando por crises existenciais\n" +
                        "   ● Precisar de apoio psicológico imediato"
        ));



        // Adicione mais autoridades conforme necessário

        AuthorityAdapter adapter = new AuthorityAdapter(authorityList, getContext());
        recyclerView.setAdapter(adapter);

        return view;



    }
}

