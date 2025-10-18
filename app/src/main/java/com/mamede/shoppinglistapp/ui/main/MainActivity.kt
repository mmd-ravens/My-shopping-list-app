package com.mamede.shoppinglistapp.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mamede.shoppinglistapp.R
import com.mamede.shoppinglistapp.databinding.ActivityMainBinding

/**
 * Main Activity e única Activity da aplicação
 * Serve como contentor para todos os fragments,configura a navegação entre os fragments
 * e a barra de ação (toolbar)
 * @property activityMainBinding é o objeto ViewBinding
 * @property navController é o controlador de navegação
 * @property appBarConfiguration é a configuração da barra de ação
 */
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Chamado quando a Activity é criada pela primeira vez.
     * É aqui que configuramos o layout, a Toolbar e inicializamos o sistema de navegação.
     *
     * @param savedInstanceState Se a Activity estiver a ser recriada após ter sido destruída,
     * este Bundle contém os dados que ela forneceu mais recentemente. Caso contrário, é nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Configuração da Toolbar
        setSupportActionBar(activityMainBinding.toolbar)

        // Configuração do controlador de navegação
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //Informa à toolbar quais são os destinos de um "nível superior"
        // do nav_graph
        //A seta "voltar" não deve aparecer
        appBarConfiguration = AppBarConfiguration(navController.graph)

        //Liga o navcontroller à actionbar da toolbar
        //faz com que o título da toolbar seja att com a "label" da fragment atual
        setupActionBarWithNavController(navController, appBarConfiguration)

        ViewCompat.setOnApplyWindowInsetsListener(activityMainBinding.mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * A função é chamada quando o usuário pressiona a seta "voltar" na barra de ação.
     * Ela usa o controlador de navegação para navegar de volta para o destino anterior.
     *
     * @return true se a navegação foi bem-sucedida, false caso contrário.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}